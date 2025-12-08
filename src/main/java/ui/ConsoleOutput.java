package ui;

import domain.game.OutputProvider;

public class ConsoleOutput implements OutputProvider {
	public void display(String message) {
		System.out.println(message);
	}
}


