package domain.game;

public class DrawFromBottomEffect implements CardEffect {
	public boolean canExecute(Game game, Player player) {
		return true;
	}

	public void execute(Game game, Player player, InputProvider input, OutputProvider output) {
		Card card = game.drawFromBottom();
		game.addCardToHand(card);
		String message = "Drew from the bottom: " + card.getCardType();
		output.display(message);
	}
}

