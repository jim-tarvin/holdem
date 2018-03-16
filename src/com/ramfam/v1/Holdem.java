package com.ramfam.v1;


public class Holdem {

	static boolean quiet = true;
	static boolean showBest = true;


	public static void main(String[] args) throws Exception {

		int players = 2;
		int games = 1;

		if (args.length > 0)
			players = Integer.parseInt(args[0]);
		if (args.length > 1)
			games = Integer.parseInt(args[1]);
		if (args.length > 2)
			quiet = Boolean.parseBoolean(args[2]);
		if (args.length > 3)
			showBest = Boolean.parseBoolean(args[3]);

		Table table = new Table(players);

		int shuffles = 20;
		for (int i = 0; i < games; i++)
			table.playGame(shuffles);

		if (games > 20)
			table.showResults(games);
	}
}


