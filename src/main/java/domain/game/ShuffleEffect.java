package domain.game;

public class ShuffleEffect implements CardEffect {
	private static final int MAX_SHUFFLES = 100;

	public boolean canExecute(EffectContext context) {
		return true;
	}

	public void execute(EffectContext context) {
		String prompt = "Enter how many times to shuffle the deck (1-" + MAX_SHUFFLES + "): ";
		int times = context.getInput().readInteger(prompt, 1, MAX_SHUFFLES);
		context.getGame().playShuffle(times);
		context.getOutput().display("Deck shuffled " + times + " times.");
	}
}


