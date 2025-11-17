package domain.game;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.security.SecureRandom;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public class DefuseExplodingKittenIntegrationTesting {
	Game game;
	Deck deck;
	Instantiator instantiator;
	Player playerZero;
	Player playerOne;
	Player playerTwo;
	Player playerThree;

	@Given("a game with {int} players with current player at index {int}")
	public void a_game_with_players_with_current_player_at_index(
			Integer numPlayers, Integer playerIdx) {
		instantiator = new Instantiator();

		final int maxDeckSize = 42;

		deck = new Deck(new ArrayList<>(), new SecureRandom(), GameType.NONE,
				0, maxDeckSize, instantiator);

		final int playerIDZero = 0;
		final int playerIDOne = 1;
		final int playerIDTwo = 2;
		final int playerIDThree = 3;
		final int numOfPlayers = numPlayers;

		playerZero = new Player(playerIDZero, instantiator);
		playerOne = new Player(playerIDOne, instantiator);
		playerTwo = new Player(playerIDTwo, instantiator);
		playerThree = new Player(playerIDThree, instantiator);
		Player[] players = new Player[]{playerZero, playerOne, playerTwo, playerThree};

		int[] turnTracker = {1, 1, 1, 1, 1};
		game = new Game(numOfPlayers, GameType.NONE, deck,
				players, new SecureRandom(),
				new ArrayList<Integer>(), turnTracker);
		game.setCurrentPlayerTurn(playerIdx);
	}

	@Given("a deck of size {int} with an exploding kitten card at the top")
	public void a_deck_of_size_with_an_exploding_kitten_card_at_the_top(Integer deckSize) {
		deck.insertCard(CardType.CAT_ONE, deckSize, false);
		deck.insertCard(CardType.EXPLODING_KITTEN, 1, false);
	}

	@Given("the player draws an exploding kitten card")
	public void the_player_draws_an_exploding_kitten_card() {
		Card explodingKittenCard = game.drawCard();
		game.addCardToHand(explodingKittenCard);
	}

	@Given("the player has a defuse card")
	public void the_player_has_a_defuse_card() {
		int currentPlayerTurn = game.getPlayerTurn();
		Card defuseCard = instantiator.createCard(CardType.DEFUSE);
		game.addCardToHand(defuseCard);
		game.playExplodingKitten(currentPlayerTurn);
	}

	@When("the player plays a defuse card wanting to insert the exploding kitten at {int}")
	public void the_player_plays_a_defuse_card_wanting_to_insert_the_exploding_kitten_at_index
			(Integer index) {
		int currentPlayerTurn = game.getPlayerTurn();
		game.playDefuse(index, currentPlayerTurn);
	}

	@Then("the player inserts the exploding kitten card into deck at {int}")
	public void current_player_inserts_the_exploding_kitten_card_into_deck_at_index
			(Integer index) {
		assertEquals(CardType.EXPLODING_KITTEN, game.getDeckCardType(index));
	}

	@Then("the player loses the defuse card")
	public void the_player_loses_the_defuse_card() {
		int currentPlayerTurn = game.getPlayerTurn();
		IllegalArgumentException exception =
				assertThrows(IllegalArgumentException.class, () -> {
					game.getIndexOfCardFromHand(currentPlayerTurn,
							CardType.DEFUSE);
				});
		assertEquals("No Card Found", exception.getMessage());
	}

	@Then("the player's turn ends")
	public void the_player_s_turn_ends() {
		final int zero_turns = 0;
		assertEquals(zero_turns, game.getNumberOfTurns());
	}

	@Then("the player is alive")
	public void the_player_is_alive() {
		Player currentPlayer = game.getPlayerAtIndex(game.getPlayerTurn());
		assertFalse(currentPlayer.getIsDead());
	}
}
