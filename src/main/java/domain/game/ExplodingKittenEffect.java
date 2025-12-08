package domain.game;

public class ExplodingKittenEffect implements CardEffect {
	public boolean canExecute(EffectContext context) {
		return true;
	}

	public void execute(EffectContext context) {
		Game game = context.getGame();
		int playerIndex = game.getPlayerTurn();
		if (!game.checkIfPlayerHasCard(playerIndex, CardType.DEFUSE)) {
			game.playExplodingKitten(playerIndex);
			context.getOutput().display("Player " + playerIndex + " exploded.");
			return;
		}
		int deckSize = game.getDeckSize();
		int index = context.getInput().readInteger(
				"Choose position to insert Exploding Kitten (0-" + deckSize + "): ",
				0,
				deckSize
		);
		game.playDefuse(index, playerIndex);
		context.getOutput().display("Player " + playerIndex + " defused the Exploding Kitten.");
	}
}


