package domain.game;

public interface InputProvider {
	int readInteger(String message, int min, int max);
	boolean readYesNo(String message);
}


