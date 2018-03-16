package com.ramfam.v1;

import java.util.ArrayList;

class Table {

	private final Player[]	players;

	private final Deck		deck = new Deck();
	private final int[]		winners	= new int[Hand.Rank.values().length];

	Table( int playerCount ) throws Exception {

		String[] names = {"Jim", "Jeff", "Michael", "Mayr", "Sam", "Meredith", "Rick", "Joe", "Chris", "Katherine", "Lan", "Jeannie", "Paul", "Brian", "Pete", "Mary", "Lizzy", "Kari", "Heather", "Mike"};
		if ( playerCount > names.length )
			throw new Exception("too many players" );

		players = new Player[playerCount];
		for ( int i = 0 ; i < playerCount ; i++ )
			players[i] = new Player(names[i]);

	}

	void showResults( final int games ) {
		for ( Player p : players ) {
			System.out.format( "%5d (%4.1f) win%1s : %s\n", p.getWins(), p.getWins()/(float)games*100, p.getWins()==1?" ":"s", p.getName() );
		}

		System.out.println("\n\n" );
		Hand.Rank[] ranks = Hand.Rank.values();
		for ( int i = 0 ; i < winners.length ; i++ ) {
			System.out.format( "%5d (%4.1f) win%1s : %s\n", winners[i], winners[i]/(float)games*100, winners[i]==1?" ":"s", ranks[i] );
		}
	}

	void playGame( int shuffles ) {

		for ( Player p : players )
			p.clear();

		deck.shuffle(shuffles);
		deal( players, 2 );

		deck.burn();
		dealCommunity( players, 3 );

		deck.burn();
		dealCommunity( players, 1 );

		deck.burn();
		dealCommunity( players, 1 );

		evaluate( players );

		if ( ! Holdem.quiet )
			print( players );
	}

	private void deal( Player[] players, int count ) {

		for ( int i = 0 ; i < count ; i++ ) {
			for (Player player : players) {
				player.add(deck.pop());
			}
		}
	}

	private void dealCommunity( Player[] players, int count ) {

		for ( int i = 0 ; i < count ; i++ ) {
			Card c = deck.pop();
			for (Player player : players) {
				player.add(c);
			}
		}
	}

	private void evaluate( Player[] players ) {

		ArrayList<Player> best = new ArrayList<>();

		int highrank = -1;

		for ( Player p : players ) {
			p.evaluate();
			if ( p.getRank().ordinal() > highrank ) {	// a better hand!
				best.clear();							// ...so clear all the lower hands out
				highrank = p.getRank().ordinal();
				best.add( p );
			}
			else if ( p.getRank().ordinal() == highrank ) {
				best.add( p );
			}
		}

		if ( ! Holdem.quiet ) {
			System.out.println( "Best Hand(s) = " + Hand.Rank.values()[highrank] );
			for ( Player p : best ) {
				System.out.println( p );
			}
			System.out.println("\n\n");
		}

		findBest( best );
		for ( Player p : best ) {
			p.win();
		}

		// only count each winning hand one time, even if multiple players had it
		winners[best.get(0).getRank().ordinal()]++;

		if ( Holdem.showBest ) {
			System.out.println("post eval: Best Hand = " + Hand.Rank.values()[highrank]);
			for (Player p : best) {
				System.out.println( p );
			}
			System.out.println("\n\n" );
		}
	}

	private void findBest(ArrayList<Player> top ) {

		int left = 0;
		int right = 1;

		while ( right < top.size() ) {
			Card[] handA = top.get(left).getFinalHand();
			Card[] handB = top.get(right).getFinalHand();

			int remove = eliminateOne( handA, handB );

			switch ( remove ) {
				case -1 :	top.remove(left);
					// if left > 0, that means the hands have matched up to this point; eliminate all of them
					while ( --left >= 0 )
						top.remove(left);
					left = 0;
					right = 1;
					break;
				case  0 :	left++;
					right++;
					break;
				case  1 :	top.remove(right);
					break;
			}
		}

		// whatever is left is/are the best hand(s)
	}

	private int eliminateOne( Card[] handA, Card[] handB ) {

		for ( int i = 0 ; i < 5 ; i++ ) {
			if (handA[i].getRank().ordinal() < handB[i].getRank().ordinal()) {
				return -1;
			} else if (handA[i].getRank().ordinal() > handB[i].getRank().ordinal()) {
				return 1;
			}
		}

		return 0;
	}

	private void print( Player[] players ) {
		for (Player player : players) {
			System.out.println(player.toString());
		}
	}
}
