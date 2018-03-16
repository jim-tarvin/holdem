package com.ramfam.v2;


import java.util.*;

class Hand {

	public enum Rank { NORANK, HIGHCARD, TWOOFAKIND, TWOPAIR, THREEOFAKIND, STRAIGHT, FLUSH, FULLHOUSE, FOUROFAKIND, STRAIGHTFLUSH }

	private State								state		= INITIAL;


	private HashMap<Card.Rank,List<Card>>		rankMap		= new HashMap<>( Card.Rank.values().length );
	private SortedSet<Card>						pairs		= new TreeSet<>();
	private Card								keyCard		= null;
	private int[]								flush		= new int[4];



	Hand() {
		for ( Card.Rank r : Card.Rank.values() ) {
			rankMap.put( r, new ArrayList<>(4) );
		}
	}
//
//	void reset() {
//		state = INITIAL;
//		keyCard = null;
//		pairs.clear();
//
//		for (Map.Entry<Card.Rank, List<Card>> entry : rankMap.entrySet()) {
//			entry.getValue().clear();
//		}
	// clear flush urray
//	}

	void add( Card c ) {
		System.out.print("Add card: " + c + "\t\t" );
		state.add( this, c );
	}


	private void set( State newState ) {
		System.out.println("State transition ("+state+" -> "+newState+")");
		state = newState;
	}

	private void setKeyCard(Card c ) {
		System.out.println("Set Key Card (old="+keyCard+" -> new="+c+")");
		keyCard = c;
	}

	private Card getKeyCard() {
		return keyCard;
	}

	private int addToHand(Card c ) {
		flush[c.getSuit().ordinal()]++;
		List<Card> list = rankMap.get( c.getRank() );
		list.add( c );
		return list.size();
	}

	private int addPair( Card c ) {

		pairs.add( c );
		if ( pairs.size() == 3 ) {			// only keep the top two pairs
			pairs.remove(pairs.last());
		}

		return pairs.size();
	}

	private void removePair( Card r ) {
		pairs.remove( r );
	}


	private static final Card.Rank[]	CardRanks = Card.Rank.values();	// i wish i had a better way to index into the rankMap in reverse...
	boolean checkForStraight() {

		for ( int i = Card.Rank.values().length - 1 ; i > 0 ; i-- ) {

			if ( i >= 4
					&& rankMap.get(CardRanks[i  ] ).size() > 0
					&& rankMap.get(CardRanks[i-1] ).size() > 0
					&& rankMap.get(CardRanks[i-2] ).size() > 0
					&& rankMap.get(CardRanks[i-3] ).size() > 0
					&& rankMap.get(CardRanks[i-4] ).size() > 0  ) {

				setKeyCard( rankMap.get(CardRanks[i]).get(0) );
				return true;

			} else if ( i == 3
					&& rankMap.get(CardRanks[i  ]).size() > 0
					&& rankMap.get(CardRanks[i-1]).size() > 0
					&& rankMap.get(CardRanks[i-2]).size() > 0
					&& rankMap.get(CardRanks[i-3]).size() > 0
					&& rankMap.get(Card.Rank.ACE ).size() > 0 ) {

				setKeyCard( rankMap.get(CardRanks[i]).get(0) );
				return true;
			}
		}

		return false;
	}

	boolean checkForFlush( Card c ) {	// brute force for now

		return flush[c.getSuit().ordinal()] > 4;
	}




	private static final State INITIAL			= new NoRank();
	private static final State HIGH_CARD		= new HighCard();
	private static final State TWO_OF_A_KIND	= new TwoOfaKind();
	private static final State TWO_PAIR			= new TwoPair();
	private static final State THREE_OF_A_KIND	= new ThreeOfaKind();
	private static final State STRAIGHT			= new Straight();
	private static final State FLUSH			= new Flush();
	private static final State FULL_HOUSE		= new FullHouse();
	private static final State FOUR_OF_A_KIND	= new FourOfaKind();
	private static final State STRAIGHT_FLUSH	= new StraightFlush();

	interface State {
		Rank getRank();
		void add(Hand hsm, Card c);
	}

	static private abstract class StateCore implements State {
		@Override
		public String toString() {
			return this.getClass().getSimpleName();
		}
	}

	static private class StraightFlush extends StateCore {
		@Override
		public Rank getRank() {
			return Rank.STRAIGHTFLUSH;
		}

		@Override
		public void add(Hand hsm, Card c) {

			hsm.setKeyCard( c );
			int count = hsm.addToHand( c );

		}
	}

	static private class FourOfaKind extends StateCore {
		@Override
		public Rank getRank() {
			return Rank.FOUROFAKIND;
		}

		@Override
		public void add(Hand hsm, Card c) {

			hsm.setKeyCard( c );
			int count = hsm.addToHand( c );

			// check for straight flush
		}
	}

	static private class FullHouse extends StateCore {
		@Override
		public Rank getRank() {
			return Rank.FULLHOUSE;
		}

		@Override
		public void add(Hand hsm, Card c) {

			hsm.setKeyCard( c );
			int count = hsm.addToHand( c );

			switch ( count ) {

				case 4 :
					hsm.set( FOUR_OF_A_KIND );
					break;

				case 2 :
					hsm.addPair( c );
					break;
			}
		}
	}

	static private class Flush extends StateCore {
		@Override
		public Rank getRank() {
			return Rank.FLUSH;
		}

		@Override
		public void add(Hand hsm, Card c) {

			hsm.setKeyCard( c );
			hsm.addToHand( c );

			// 4 of a kind not possible
			// full house not possible
		}
	}

	static private class Straight extends StateCore {
		@Override
		public Rank getRank() {
			return Rank.STRAIGHT;
		}

		@Override
		public void add(Hand hsm, Card c) {

			hsm.setKeyCard( c );
			hsm.addToHand( c );

			if ( hsm.checkForFlush(c) ) {
				hsm.set(FLUSH);
			}

			// 4 of a kind not possible
			// full house not possible
		}
	}

	static private class ThreeOfaKind extends StateCore {
		@Override
		public Rank getRank() {
			return Rank.THREEOFAKIND;
		}

		@Override
		public void add(Hand hsm, Card c) {

			hsm.setKeyCard( c );
			int count = hsm.addToHand( c );

			if ( hsm.checkForFlush(c) ) {

				hsm.set(FLUSH);

			} else if ( hsm.checkForStraight() ) {

				hsm.set(STRAIGHT);

			} else {

				switch (count) {

					case 4:
						hsm.set(FOUR_OF_A_KIND);
						break;

					case 2:
						hsm.addPair(c);
						hsm.set(FULL_HOUSE);
						break;
				}
			}
		}
	}

	static private class TwoPair extends StateCore {
		@Override
		public Rank getRank() {
			return Rank.TWOPAIR;
		}

		@Override
		public void add(Hand hsm, Card c) {

			hsm.setKeyCard( c );
			int count = hsm.addToHand( c );

			if ( hsm.checkForFlush(c) ) {

				hsm.set(FLUSH);

			} else if ( hsm.checkForStraight() ) {

				hsm.set(STRAIGHT);

			} else {

				switch (count) {

					case 3:
						hsm.set(FULL_HOUSE);
						break;

					case 2:
						hsm.addPair(c);
						break;

				}
			}
		}
	}

	static private class TwoOfaKind extends StateCore {
		@Override
		public Rank getRank() {
			return Rank.TWOOFAKIND;

		}

		@Override
		public void add(Hand hsm, Card c) {

			hsm.setKeyCard( c );
			int count = hsm.addToHand( c );

			if ( hsm.checkForFlush(c) ) {

				hsm.set(FLUSH);

			} else if ( hsm.checkForStraight() ) {

				hsm.set(STRAIGHT);

			} else {

				switch (count) {

					case 3:                                // this rank is no longer (just) a pair
						hsm.removePair(c);
						hsm.set(THREE_OF_A_KIND);
						break;

					case 2:                                // we have a new pair
						if (hsm.addPair(c) == 2) {
							hsm.set(TWO_PAIR);
						}
						break;
				}
			}
		}
	}

	static private class HighCard extends StateCore {
		@Override
		public Rank getRank() {
			return Rank.HIGHCARD;
		}

		@Override
		public void add(Hand hsm, Card c) {

			Card key = hsm.getKeyCard();

			if ( key == null || c.getRank().ordinal() > key.getRank().ordinal() )
				hsm.setKeyCard( c );

			int count = hsm.addToHand( c );

			if ( hsm.checkForFlush(c) ) {

				hsm.set(FLUSH);

			} else if ( hsm.checkForStraight() ) {

				hsm.set(STRAIGHT);

			} else if ( count == 2 ) {

				hsm.addPair( c );
				hsm.set(TWO_OF_A_KIND);

			}
		}
	}

	static private class NoRank extends StateCore {
		@Override
		public Rank getRank() {
			return Rank.NORANK;
		}

		@Override
		public void add(Hand hsm, Card c) {
			hsm.set( HIGH_CARD );
			hsm.setKeyCard( c );
			hsm.addToHand( c );
		}
	}

}