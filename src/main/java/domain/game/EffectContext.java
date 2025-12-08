package domain.game;

public class EffectContext {
	private final Game game;
	private final InputProvider input;
	private final OutputProvider output;

	public EffectContext(Game game, InputProvider input, OutputProvider output) {
		this.game = game;
		this.input = input;
		this.output = output;
	}

	public Game getGame() {
		return game;
	}

	public InputProvider getInput() {
		return input;
	}

	public OutputProvider getOutput() {
		return output;
	}
}


