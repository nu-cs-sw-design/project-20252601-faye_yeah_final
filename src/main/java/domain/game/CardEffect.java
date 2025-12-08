package domain.game;

public interface CardEffect {
	boolean canExecute(EffectContext context);
	void execute(EffectContext context);
}


