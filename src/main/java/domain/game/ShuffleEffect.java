package domain.game;

public class ShuffleEffect implements CardEffect {
	private static final int MAX_SHUFFLES = 100;

	public boolean canExecute(Game game, Player player) {
		return true;
	}

	public void execute(Game game, Player player, InputProvider input, OutputProvider output) {
		String prompt = "Enter how many times to shuffle the deck (1-" + MAX_SHUFFLES + "): ";
		int times = input.readInteger(prompt, 1, MAX_SHUFFLES);
		game.playShuffle(times);
		output.display("Deck shuffled " + times + " times.");
	}
}

