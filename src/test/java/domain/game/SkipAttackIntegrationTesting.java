package domain.game;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.ArrayList;
import java.security.SecureRandom;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SkipAttackIntegrationTesting {
	Game game;
	Deck deck;
	Instantiator instantiator;
	Player playerZero;
	Player playerOne;
	Player playerTwo;
	Player playerThree;

	@Given("there are {int} players, with the game " +
		"starting at the first player's turn")
	public void
	there_are_players_with_the_game_starting_at_the_first_player_s_turn
		(Integer numOfPlayers) {
		instantiator = new Instantiator();

		final int maxDeckSize = 42;

		deck = new Deck(new ArrayList<>(), new SecureRandom(), GameType.NONE,
				0, maxDeckSize, instantiator);

		final int playerIDZero = 0;
		final int playerIDOne = 1;
		final int playerIDTwo = 2;
		final int playerIDThree = 3;

		playerZero = new Player(playerIDZero, instantiator);
		playerOne = new Player(playerIDOne, instantiator);
		playerTwo = new Player(playerIDTwo, instantiator);
		playerThree = new Player(playerIDThree, instantiator);
		Player[] players = new Player[] {playerZero, playerOne, playerTwo, playerThree};
		players[0].addCardToHand(instantiator.createCard(CardType.SKIP));

		int[] turnTracker = {1, 1, 1, 1, 1};
		game = new Game(numOfPlayers, GameType.NONE, deck,
				players, new SecureRandom(),
				new ArrayList<Integer>(), turnTracker);

	}

	@Given("{int} attacks have been played")
	public void attacks_have_been_played(Integer numberOfAttacks) {
		final int normalAttack = 5;
		for (int i = 0; i < numberOfAttacks; i++) {
			game.addAttackQueue(normalAttack);
		}

		game.startAttackPhase();
		game.setTurnToTargetedIndexIfAttackOccurred();
		game.setPlayerNumberOfTurns();
	}

	@When("the correct player who has been attacked plays {int} skips")
	public void the_correct_player_who_has_been_attacked_plays_skips(Integer numberOfSkips) {
		for (int i = 0; i < numberOfSkips; i++) {
			game.playSkip(false);
		}
	}

	@Then("the correct player {int} has their number of turns" +
			" reduced to {int}.")
	public void
	the_correct_player_has_their_number_of_turns_reduced_to
		(Integer playerTurn, Integer numberOfTurns) {
		assertEquals(game.getPlayerTurn(), playerTurn);
		assertEquals(game.getNumberOfTurns(), numberOfTurns);
	}
}
