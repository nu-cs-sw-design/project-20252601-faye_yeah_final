package domain.game;

public class ExplodingKittenEffect implements CardEffect {
	public boolean canExecute(Game game, Player player) {
		return true;
	}

	public void execute(Game game, Player player, InputProvider input, OutputProvider output) {
		int playerIndex = game.getPlayerTurn();
		if (!game.checkIfPlayerHasCard(playerIndex, CardType.DEFUSE)) {
			game.playExplodingKitten(playerIndex);
			output.display("Player " + playerIndex + " exploded.");
			return;
		}
		int deckSize = game.getDeckSize();
		int index = input.readInteger(
				"Choose position to insert Exploding Kitten (0-" + deckSize + "): ",
				0,
				deckSize
		);
		game.playDefuse(index, playerIndex);
		output.display("Player " + playerIndex + " defused the Exploding Kitten.");
	}
}

