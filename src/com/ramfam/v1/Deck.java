package com.ramfam.v1;

import java.util.ArrayList;
import java.util.Collections;

class Deck {

	private ArrayList<Card> deck = new ArrayList<>(52);
	private int next;

	Deck() {
		int i = 0;
		for ( Card.Suit s : Card.Suit.values() )
			for ( Card.Rank r : Card.Rank.values () )
				deck.add(new Card( r, s ));
	}

	void shuffle( int count ) {
		next = 0;
		Collections.shuffle( deck );
	}


	Card pop() {
		return deck.get( next++ );
	}

	void burn() {
		next++;
	}
}
