package domain.game;

public class DrawFromBottomEffect implements CardEffect {
	public boolean canExecute(EffectContext context) {
		return true;
	}

	public void execute(EffectContext context) {
		Game game = context.getGame();
		Card card = game.drawFromBottom();
		game.addCardToHand(card);
		String message = "Drew from the bottom: " + card.getCardType();
		context.getOutput().display(message);
	}
}


