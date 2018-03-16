package com.ramfam.v1;

import java.util.*;

public class Hand {

	public enum Rank { HIGHCARD, PAIR, TWOPAIR, SET, STRAIGHT, FLUSH, FULLHOUSE, FOUROFAKIND, STRAIGHTFLUSH }


	private Card[]	hand;
	private int		count;

	private Ranked		ranked	= new Ranked();



	private static final Card.Rank[]	CardRanks = Card.Rank.values();


	Hand(int cards ) {
		this.count = 0;
		hand = new Card[cards];
		clear();
	}

	void clear() {
		for ( int i = 0 ; i < count ; i++ )
			hand[i] = null;
		count = 0;
		ranked.clear();
	}

	/**
	 * as a card is added:
	 * 		evaluate current hand (if 5 cards or more)
	 * 		evaluate best possible hand if more cards to come
	 * 		evaluate outs
	 * 		calculate odds
	 */
	void add( Card c ) {
		hand[count++] = c;
		ranked.add( c );
	}

	void evaluate() {
		ranked.evaluate();
	}

	Rank getRank() {
		return ranked.getRank();
	}

	Card[] getFinalHand() {
		return ranked.getFinalHand();
	}

	public String toString() {

		StringBuilder out = new StringBuilder();

		for ( int i = 0 ; i < count ; i++ )
			out.append(hand[i].toString()).append(" ");

		out.append(" :: Rank: ").append(String.format("%-13s", ranked.getRank().toString()));
		out.append(" ").append(ranked.toString()).append("]");

		return out.toString();
	}

	private class Ranked {

		private HashMap<Card.Rank,List<Card>>	rankMap		= new HashMap<>(Card.Rank.values().length);
		private Card[]							finalHand	= new Card[5];

		private Card							flush		= null;
		private Card.Rank						straight	= null;			// highest rank in straight

		private Card.Rank						high		= null;
		private ArrayList<Card.Rank>			pairs		= new ArrayList<>(2);

		private Card.Rank						set			= null;
		private Card.Rank						quads		= null;

		private int								handrank	= Rank.HIGHCARD.ordinal();

		Ranked() {
			for ( Card.Rank r : Card.Rank.values() ) {
				rankMap.put( r, new ArrayList<>(4) );
			}
		}

		Card[] getFinalHand() {
			return finalHand;
		}

		void add( Card c ) {

			Card.Rank		cardRank		= c.getRank();
			List<Card>		cardList		= rankMap.get(cardRank);

			cardList.add( c );		// add this card to a list of the same rank/ordinal
			checkForFlush(c.getSuit());
			checkForStraight();

			int				cardListCount	= cardList.size();

			if ( cardListCount == 4 ) {
				handrank = Rank.FOUROFAKIND.ordinal();
				quads = cardRank;

			} else if ( handrank < Rank.FLUSH.ordinal() && flush != null ) {
				handrank = Rank.FLUSH.ordinal();

			} else if ( handrank < Rank.STRAIGHT.ordinal() && straight != null ) {
				handrank = Rank.STRAIGHT.ordinal();

			} else if ( cardListCount == 3 ) {

				// do we already have a set?
				// 		if 'yes', AND if this set is bigger, demote the existing set to a pair
				//		if 'no', then this set remains in the 'pair' list
				if ( set != null ) {
					if ( cardRank.ordinal() > set.ordinal() ) {
						// demote 'set' to just a pair...make sure to put it in the correct/ordered position
					} else {
						// don't do anything, this rank is lower than the existing set AND is already marked as a pair
					}
				} else {
					// this is the first set; promote it from a pair to a set
					pairs.remove(cardRank);
					set = cardRank;
				}

				int test = pairs.size() > 0 ? Rank.FULLHOUSE.ordinal() : Rank.SET.ordinal();	// don't accidentally downgrade the hand
				handrank = Math.max( test, handrank );

			} else if ( cardListCount == 2 ) {

				int			i		= 0;
				int			size	= pairs.size();

				// add sorted, highest rank first
				for ( ; i < size && pairs.get(i).ordinal() > cardRank.ordinal() ; i++ ) {
				}
				pairs.add( i, cardRank );

				int test = (set != null ? Rank.FULLHOUSE.ordinal() : (pairs.size() > 1 ? Rank.TWOPAIR.ordinal() : Rank.PAIR.ordinal()));

				handrank = Math.max( test, handrank );

			} else if ( high == null || cardRank.ordinal() > high.ordinal() ) {
				high = cardRank;
			}
		}

		void checkForFlush( Card.Suit suit ) {

			if ( flush != null && suit != flush.getSuit() )
				return;

			// re-evaluate to ensure we have the highest-order flush
			flush = null;

			// we do not have a flush, so go through the ranked cards, counting the same 'offset'/ordinal for each rank

			List<Card>	currentRank;
			int count = 0;

			for ( int i = CardRanks.length - 1 ; count < 5 && i >= 0 ; i-- ) {
				currentRank = rankMap.get( CardRanks[i] );
				for ( Card c : currentRank ) {
					if ( c.getSuit() == suit ) {
						if ( flush == null ) {
							flush = c;    // hold the first card of same suit we find; it is the highest card of the flush
						}
						count++;
					}
				}
			}

			if ( count < 5 ) {
				flush = null;    // still no flush
			}
		}

		void checkForStraight() {

			// re-evaluate to ensure we have the highest-order straight
			straight = null;

			for ( int i = Card.Rank.values().length - 1 ; i > 0 && straight == null ; i-- ) {

				if ( i >= 4
						&& rankMap.get(CardRanks[i  ] ).size() > 0
						&& rankMap.get(CardRanks[i-1] ).size() > 0
						&& rankMap.get(CardRanks[i-2] ).size() > 0
						&& rankMap.get(CardRanks[i-3] ).size() > 0
						&& rankMap.get(CardRanks[i-4] ).size() > 0  ) {

					straight = CardRanks[i];

				} else if ( i == 3
						&& rankMap.get(CardRanks[i  ]).size() > 0
						&& rankMap.get(CardRanks[i-1]).size() > 0
						&& rankMap.get(CardRanks[i-2]).size() > 0
						&& rankMap.get(CardRanks[i-3]).size() > 0
						&& rankMap.get(Card.Rank.ACE ).size() > 0 ) {

					straight = CardRanks[i];
				}
			}
		}

		Rank getRank() {
			return Rank.values()[handrank];
		}

		void clear() {
			pairs.clear();
			high = set = quads = null;
			flush = null;
			handrank = Rank.HIGHCARD.ordinal();

			for ( Map.Entry<Card.Rank,List<Card>> entry : rankMap.entrySet() ) {
				entry.getValue().clear();
			}
		}

		void evaluate() {

			switch ( getRank() ) {
				case STRAIGHTFLUSH :
					break;

				case FOUROFAKIND :
					move( rankMap.get(quads), finalHand, 0, 4 );
					fill( finalHand, 4, rankMap.size()-1, 1 );
					break;

				case FULLHOUSE :
					move( rankMap.get(set),          finalHand, 0, 3 );
					move( rankMap.get(pairs.get(0)), finalHand, 3, 2 );
					break;

				case FLUSH :
					move( finalHand, flush.getSuit() );
					break;

				case STRAIGHT :
					if ( straight.ordinal() > 3 )
						fill( finalHand, 0, straight.ordinal(), 5 );
					else {
						fill(finalHand, 0,  straight.ordinal(), 4);
						fill(finalHand, 4, rankMap.size()-1, 1);	// should grab the ACE
					}
					break;

				case SET :
					move( rankMap.get(set), finalHand, 0, 3 );
					fill( finalHand, 3, rankMap.size()-1, 2 );
					break;

				case TWOPAIR :
					move( rankMap.get(pairs.get(0)), finalHand, 0, 2 );
					move( rankMap.get(pairs.get(1)), finalHand, 2, 2 );
					fill( finalHand, 4, rankMap.size()-1, 1 );
					break;

				case PAIR :
					move( rankMap.get(pairs.get(0)), finalHand, 0, 2 );
					fill( finalHand, 2, rankMap.size()-1, 3 );
					break;

				default :
					fill( finalHand, 0, rankMap.size()-1, 5 );
					break;
			}
		}

		private void move( List<Card> source, Card[] target, int position, int count ) {
			for ( int i = 0 ; i < count ; i++ ) {
				target[position++] = source.get(i);
			}
			source.clear();		// in the case of having received 2 completes sets, this will remove one too many cards
		}

		// special move for a flush
		void move( Card[] target, Card.Suit suit ) {
			List<Card>	currentRank;
			int count = 0;

			for ( int i = CardRanks.length - 1 ; count < 5 && i >= 0 ; i-- ) {
				currentRank = rankMap.get( CardRanks[i] );
				for ( Card c : currentRank ) {
					if ( c.getSuit() == suit ) {
						target[count++] = c;
						currentRank.remove(c);
						break;
					}
				}
			}

		}

		// fill the target hand with the remaining highest cards
		// this method assumes that the Card Map only has cards that aren't already in the target hand
		private void fill( Card[] target, int targetPos, int sourcePos, int numberOfCardsToFill ) {
			for ( int i = sourcePos ; numberOfCardsToFill > 0 && i >= 0 ; i-- ) {
				if ( rankMap.get(CardRanks[i]).size() > 0 ) {
					target[targetPos++] = rankMap.get(CardRanks[i]).remove(0);
					numberOfCardsToFill--;
				}
			}
		}

		public String toString() {
			// |AC AH|6S 6C 6D|
			StringBuilder out	= new StringBuilder("[");

			for ( int i = 0 ; i < 5 ; i++ ) {
				out.append(finalHand[i]).append(i < 4 ? " " : "");
			}
			out.append("] -> ");

			if ( quads != null )
			{
				out.append("4 ").append(quads.getAbbreviation()).append("s");
			}
			else if ( set != null && pairs.size() > 0 )
			{
				out.append("Full House ").append(set.getAbbreviation()).append("s full of ").append(pairs.get(0).getAbbreviation()).append("s");

			}
			else if ( flush != null )
			{
				out.append(flush.getRank()).append(" high ").append(flush.getSuit()).append(" flush");
			}
			else if (ranked.straight != null ) {

				out.append(ranked.straight).append(" high straight");
			}
			else if ( set != null)
			{
				out.append("3 ").append(set.getAbbreviation()).append("s");
			}
			else if ( pairs.size() > 1 )
			{
				out.append(pairs.get(0).getAbbreviation()).append("s and ").append(pairs.get(1).getAbbreviation()).append("s");
			}
			else if ( pairs.size() == 1 )
			{
				out.append("pair of ").append(pairs.get(0).getAbbreviation()).append("s");
			}
			else
			{
				out.append(high).append(" high");
			}

			return out.toString();
		}
	}
}
