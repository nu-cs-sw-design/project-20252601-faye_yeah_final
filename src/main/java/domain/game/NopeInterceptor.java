package domain.game;

public class NopeInterceptor implements CardEffect {
	private final CardEffect wrapped;

	public NopeInterceptor(CardEffect wrapped) {
		this.wrapped = wrapped;
	}

	public boolean canExecute(EffectContext context) {
		return wrapped.canExecute(context);
	}

	public void execute(EffectContext context) {
		Game game = context.getGame();
		InputProvider input = context.getInput();
		OutputProvider output = context.getOutput();
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
			wrapped.execute(context);
		} else {
			output.display("Action was cancelled by NOPE.");
		}
	}
}


