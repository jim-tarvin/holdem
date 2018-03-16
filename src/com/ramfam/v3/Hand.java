package com.ramfam.v3;


import java.util.ArrayList;

class Hand {


	private ArrayList<ArrayList<Card>>	cards = new ArrayList<>(4);

	Hand() {

		for ( int i = 0 ; i < 4 ; i++ )
			cards.add( new ArrayList<>(13) );

	}

	void add( Card card ) {

		int sOrd = card.getSuit().ordinal();
		ArrayList<Card> suit = cards.get(sOrd);


		System.out.println("After insert:  " );
		for ( Card c : suit )
			System.out.print( c + "  " );

		System.out.println("+++++++");

	}














	private Card[][] cardsX = new Card[4][13];	// actual cardsX in the hand
	private int[] suitsX = new int[4];		// count of each suit in the hand
	private int[] ranksX = new int[13];		// count of each rank in the hand

	private int flushX = -1;
	private int straightX = -1;

	void addX( Card card ) {

		int rank = card.getRank().ordinal();
		int suit = card.getSuit().ordinal();

		cardsX[suit][rank] = card;

		ranksX[rank]++;
		suitsX[suit]++;

		if ( ranksX[rank] == 4 ) {

		}

		if ( flushX == -1 && suitsX[suit] > 4 ) {
			flushX = suit;
		}

		int straightcount = 0;

		// check for a straightX
		int high = Math.min( 12, rank + 4 );		// 0 = 2, 1 = 3, ...
		int low = Math.max( rank - 4, 0 );
		int current = high;

		for ( ; current >= low && straightcount < 5 ; current-- ) {
			if ( ranksX[current] > 0 ) {
				straightcount++;
			} else {
				straightcount = 0;		// no cardsX at this rank, reset to zero
			}
		}

		if ( straightcount > 4 ) {		// todo: account for ace-low straights
		}
	}

}