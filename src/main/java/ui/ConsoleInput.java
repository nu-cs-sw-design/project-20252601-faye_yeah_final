package ui;

import domain.game.InputProvider;

import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class ConsoleInput implements InputProvider {
	private final Scanner scanner;

	public ConsoleInput() {
		this.scanner = new Scanner(System.in, StandardCharsets.UTF_8);
	}

	public int readInteger(String message, int min, int max) {
		while (true) {
			System.out.print(message);
			String line = scanner.nextLine();
			try {
				int value = Integer.parseInt(line.trim());
				if (value >= min && value <= max) {
					return value;
				}
			} catch (NumberFormatException ignored) {
			}
		}
	}

	public boolean readYesNo(String message) {
		while (true) {
			System.out.print(message);
			String line = scanner.nextLine().trim().toLowerCase();
			if (line.equals("y") || line.equals("yes") || line.equals("1")) {
				return true;
			}
			if (line.equals("n") || line.equals("no") || line.equals("2")) {
				return false;
			}
		}
	}
}


