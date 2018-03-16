package com.ramfam.v1;

public class Player {

	private Hand hand;
	private String	name;
	private int		wins = 0;

	Player( String name ) {
		this.name = name;
		hand = new Hand( 7 );
	}

	String getName() {
		return name;
	}

	int getWins() {
		return wins;
	}

	void clear() {
		hand.clear();
	}

	void add( Card c ) {
		hand.add(c);
	}

	void win() {
		wins++;
	}

	void evaluate() {
		hand.evaluate();
	}

	Card[] getFinalHand() {
		return hand.getFinalHand();
	}

	Hand.Rank getRank() {
		return hand.getRank();
	}

	public String toString() {
		String out = String.format( "%-10s",name );
		return out + " = " + hand.toString();
	}
}
