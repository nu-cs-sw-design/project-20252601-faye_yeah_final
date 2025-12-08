package domain.game;

public class NopeInterceptor implements CardEffect {
	private final NopableEffect wrapped;

	public NopeInterceptor(NopableEffect wrapped) {
		this.wrapped = wrapped;
	}

	public boolean canExecute(Game game, Player player) {
		return wrapped.canExecute(game, player);
	}

	public void execute(Game game, Player player, InputProvider input, OutputProvider output) {
		int sourcePlayer = game.getPlayerTurn();
		boolean cancelled = false;

		for (int i = 0; i < game.getNumberOfPlayers(); i++) {
			if (i == sourcePlayer) {
				continue;
			}
			if (game.checkIfPlayerDead(i)) {
				continue;
			}
			if (!game.checkIfPlayerHasCard(i, CardType.NOPE)) {
				continue;
			}
			boolean playNope = input.readYesNo("Player " + i + " has a NOPE. Play it? (y/n): ");
			if (playNope) {
				game.removeCardFromHand(i, CardType.NOPE);
				output.display("Player " + i + " played NOPE.");
				cancelled = true;
				break;
			}
		}

		if (!cancelled) {
			wrapped.execute(game, player, input, output);
		} else {
			output.display("Action was cancelled by NOPE.");
		}
	}
}

