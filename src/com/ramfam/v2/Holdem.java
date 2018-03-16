package com.ramfam.v2;


public class Holdem {

	static boolean quiet = true;
	static boolean showBest = true;


	public static void main( String[] args ) throws Exception {

		int players = 2;
		int games = 1;

		if ( args.length > 0 )
			players = Integer.parseInt(args[0]);
		if ( args.length > 1 )
			games = Integer.parseInt(args[1]);
		if ( args.length > 2 )
			quiet = Boolean.parseBoolean(args[2]);
		if ( args.length > 3 )
			showBest = Boolean.parseBoolean(args[3]);


		Hand hsm = new Hand();

		hsm.add( new Card(Card.Rank.FOUR, Card.Suit.HEART));
		hsm.add( new Card(Card.Rank.ACE, Card.Suit.CLUB));
		hsm.add( new Card(Card.Rank.SEVEN, Card.Suit.SPADE));
		hsm.add( new Card(Card.Rank.SEVEN, Card.Suit.DIAMOND));
		hsm.add( new Card(Card.Rank.NINE, Card.Suit.SPADE));
		hsm.add( new Card(Card.Rank.NINE, Card.Suit.CLUB));
		hsm.add( new Card(Card.Rank.ACE, Card.Suit.HEART));
		hsm.add( new Card(Card.Rank.EIGHT, Card.Suit.HEART));
		hsm.add( new Card(Card.Rank.FIVE, Card.Suit.HEART));
		hsm.add( new Card(Card.Rank.ACE, Card.Suit.DIAMOND));
		hsm.add( new Card(Card.Rank.ACE, Card.Suit.SPADE));
		hsm.add( new Card(Card.Rank.SIX, Card.Suit.HEART));

	}
}



