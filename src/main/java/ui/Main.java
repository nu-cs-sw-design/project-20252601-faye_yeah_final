package ui;

import domain.game.Instantiator;
import domain.game.Card;
import domain.game.CardType;
import domain.game.Deck;
import domain.game.Game;
import domain.game.GameType;
import domain.game.Player;

import java.util.ArrayList;
import java.security.SecureRandom;

public class Main {
	public static void main(String[] args) {
		final int playerIDZero = 0;
		final int playerIDOne = 1;
		final int playerIDTwo = 2;
		final int playerIDThree = 3;
		final int playerIDFour = 4;
		final int maxDeckSize = 42;
		Instantiator instantiator = new Instantiator();

		Deck deck = new domain.game.Deck(new ArrayList<>(), new SecureRandom(),
				GameType.NONE, 0, maxDeckSize, instantiator);
		Player[] players = {new Player(playerIDZero, instantiator),
				new Player(playerIDOne, instantiator),
				new Player(playerIDTwo, instantiator),
				new Player(playerIDThree, instantiator),
				new Player(playerIDFour, instantiator)};
		int[] turnTracker = {1, 1, 1, 1, 1};
		Game game = new Game(0, GameType.NONE, deck,
				players, new SecureRandom(),
				new ArrayList<Integer>(), turnTracker);
		GameUI gameUI = new GameUI(game);
		gameUI.chooseLanguage();
		gameUI.chooseGame();
		gameUI.chooseNumberOfPlayers();
		for (int playerCounter = 0; playerCounter <
				game.getNumberOfPlayers(); playerCounter++) {
			game.getPlayerAtIndex(playerCounter).addDefuse(new Card(CardType.DEFUSE));
		}
		game.getDeck().initializeDeck();
		game.getDeck().shuffleDeck();
		final int cardDrawnPerPlayer = 5;
		for (int cardDrawnCounter = 0;
			cardDrawnCounter < cardDrawnPerPlayer; cardDrawnCounter++) {
			for (int playerCtr = 0; playerCtr <
					game.getNumberOfPlayers(); playerCtr++) {
				Player current = game.getPlayerAtIndex(playerCtr);
				current.addCardToHand(game.getDeck().drawCard());
			}
		}
		if (game.getGameType() == GameType.STREAKING_KITTENS) {
			game.getDeck().insertCard(CardType.EXPLODING_KITTEN,
					game.getNumberOfPlayers(), false);
		} else {
			game.getDeck().insertCard(CardType.EXPLODING_KITTEN,
					game.getNumberOfPlayers() - 1, false);
		}
		if (game.getGameType() == GameType.IMPLODING_KITTENS) {
			game.getDeck().insertCard(CardType.IMPLODING_KITTEN,
					1, false);
		}

		game.playShuffle(1);

		while (!gameUI.checkIfGameOver()) {
			gameUI.startTurn();
		}
		gameUI.endGame();
	}
}
