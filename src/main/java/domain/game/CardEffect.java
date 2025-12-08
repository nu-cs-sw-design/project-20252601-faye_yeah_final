package domain.game;

public interface CardEffect {
	boolean canExecute(Game game, Player player);
	void execute(Game game, Player player, InputProvider input, OutputProvider output);
}

