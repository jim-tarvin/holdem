package com.ramfam.v1;



/**
 * A	12
 * K	11
 * Q	10
 * J	9
 * T	8
 * 9	7
 * 8	6
 * 7	5
 * 6	4
 * 5	3	5
 * 4	2	4
 * 3	1	3
 * 2	0	2
 * A		1
 *
 */

public class Card implements Comparable<Card> {

	public enum Suit {
		DIAMOND, SPADE, HEART, CLUB;

		private final char abbreviation;

		Suit() {
			this.abbreviation = toString().charAt(0);
		}
	}

	public enum Rank {
		TWO('2'), THREE('3'), FOUR('4'), FIVE('5' ),
		SIX('6'), SEVEN('7'), EIGHT('8'), NINE('9'),
		TEN('T'), JACK('J'), QUEEN('Q'), KING( 'K' ), ACE( 'A' );

		private final char abbreviation;

		Rank( char abbreviation ) {
			this.abbreviation = abbreviation;
		}

		public char getAbbreviation() { return abbreviation; }
	}


	private final Suit		suit;
	private final Rank		rank;
	private final String	name;

	Card(Rank r, Suit s) {
		suit = s;
		rank = r;
		name = "" + r.abbreviation + s.abbreviation;
	}

	public String toString() {
		return name;
	}

	Suit getSuit() { return suit; }

	Rank getRank() { return rank; }

	@Override
	public int compareTo(Card o) {
		return o.getRank().ordinal() - rank.ordinal();
	}
}
