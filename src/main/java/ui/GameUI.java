package ui;

import domain.game.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.text.MessageFormat;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.Scanner;
import java.nio.charset.StandardCharsets;


public class GameUI {
	private Game game;
	private ResourceBundle messages;

	public GameUI (Game game) { this.game = game; }

	public void chooseLanguage() {
		Scanner scanner = new Scanner(System.in, StandardCharsets.UTF_8);
		final String language  = "1. English\n2. 한국어\n";
		final String askLanguage = "Enter the number to choose the language:";
		final String invalidChoice = "Invalid choice. Please enter 1 or 2.";
		System.out.println(language);
		System.out.println(askLanguage);

		while (true) {
			String userInput = scanner.nextLine();
			switch (userInput) {
				case "1":
					messages = ResourceBundle.getBundle
						("message", new Locale("en"));
					final String languageSetEnglish = messages.getString
						("setLanguage");
					System.out.println(languageSetEnglish);
					return;
				case "2":
					messages = ResourceBundle.getBundle
						("message", new Locale("ko"));
					final String languageSetKorean = messages.getString
						("setLanguage");
					System.out.println(languageSetKorean);
					return;
				default:
					System.out.println(invalidChoice);
			}
		}
	}

	public void chooseGame() {
		Scanner scanner = new Scanner(System.in, StandardCharsets.UTF_8);
		final String gameModePrompt = messages.getString("gameModePrompt");
		final String gameModeExplodingOption =
				messages.getString("gameModeExplodingOption");
		final String gameModeImplodingOption =
				messages.getString("gameModeImplodingOption");
		final String gameModeStreakingOption =
				messages.getString("gameModeStreakingOption");
		final String gameModeChoicePrompt = messages.getString("gameModeChoicePrompt");
		final String gameModeInvalid = messages.getString("gameModeInvalid");

		System.out.println(gameModePrompt);
		System.out.println(gameModeExplodingOption);
		System.out.println(gameModeImplodingOption);
		System.out.println(gameModeStreakingOption);

		while (true) {
			System.out.print(gameModeChoicePrompt);
			String userInput = scanner.nextLine();
			switch (userInput) {
				case "1":
					game.retrieveGameMode(GameType.EXPLODING_KITTENS);
					final String gameModeExploding =
						messages.getString("gameModeExploding");
					System.out.println(gameModeExploding);
					return;
				case "2":
					game.retrieveGameMode(GameType.IMPLODING_KITTENS);
					final String gameModeImploding =
						messages.getString("gameModeImploding");
					System.out.println(gameModeImploding);
					return;
				case "3":
					game.retrieveGameMode(GameType.STREAKING_KITTENS);
					final String gameModeStreaking =
						messages.getString("gameModeStreaking");
					System.out.println(gameModeStreaking);
					return;
				default:
					System.out.println(gameModeInvalid);
			}
		}
	}

	public void chooseNumberOfPlayers() {
		Scanner scanner = new Scanner(System.in, StandardCharsets.UTF_8);
		final String numOfPlayersPrompt = messages.getString("numOfPlayersPrompt");
		final String numOfPlayersTwo = messages.getString("numOfPlayersTwo");
		final String numOfPlayersThree = messages.getString("numOfPlayersThree");
		final String numOfPlayersFour = messages.getString("numOfPlayersFour");
		final String invalidPlayersNum = messages.getString("invalidPlayersNum");

		System.out.println(numOfPlayersPrompt);

		while (true) {
			String userInput = scanner.nextLine();
			final int twoPlayers = 2;
			final int threePlayers = 3;
			final int fourPlayers = 4;
			switch (userInput) {
				case "2":
					game.setNumberOfPlayers(twoPlayers);
					System.out.println(numOfPlayersTwo);
					return;
				case "3":
					game.setNumberOfPlayers(threePlayers);
					System.out.println(numOfPlayersThree);
					return;
				case "4":
					game.setNumberOfPlayers(fourPlayers);
					System.out.println(numOfPlayersFour);
					return;
				default:
					System.out.println(invalidPlayersNum);
			}
		}
	}

	private int checkValidPlayerIndexInput() {
		Scanner scanner = new Scanner(System.in, StandardCharsets.UTF_8);
		final String targetedAttackPrompt = messages.getString("targetedAttackPrompt");
		final String userPlayedCardAtIndex = messages.getString("userPlayedCardAtIndex");
		final String playerChooseDifferent = messages.getString("playerChooseDifferent");
		final String invalidIndex = messages.getString("invalidIndex");
		final String invalidNumber = messages.getString("invalidNumber");

		while (true) {
			System.out.println(targetedAttackPrompt);
			String userInputTwo = scanner.nextLine();
			try {
				int userIndex = Integer.parseInt(userInputTwo);
				if (checkUserWithinBounds(userIndex)) {
					if (checkIfPlayerIsAlive(userIndex)) {
						final String formattedMessage = MessageFormat.format
							(userPlayedCardAtIndex, userIndex);
						System.out.println(formattedMessage);
						return userIndex;
					} else {
						System.out.println(playerChooseDifferent);
					}
				} else {
					final String formattedMessage = MessageFormat.format
						(invalidIndex, game.getNumberOfPlayers() - 1);
					System.out.println(formattedMessage);
				}
			} catch (NumberFormatException e) {
				System.out.println(invalidNumber);
			}
		}
	}


	private void startAttackFollowUp(boolean targeted) {
		final int normalAttack = 5;
		Scanner scanner = new Scanner(System.in, StandardCharsets.UTF_8);
		int tempTurn;
		tempTurn = game.getPlayerTurn();
		tempTurn = (tempTurn + 1) % game.getNumberOfPlayers();
		if (targeted) {
			tempTurn = checkValidPlayerIndexInput();
			game.addAttackQueue(tempTurn);
		} else {
			game.addAttackQueue(normalAttack);
		}
		while (hasCard(tempTurn, CardType.ATTACK)
				|| hasCard(tempTurn, CardType.TARGETED_ATTACK)) {

			if (hasCard(tempTurn, CardType.ATTACK)) {
				final String attackCardPrompt = MessageFormat.format(
					messages.getString("attackCardPrompt"), tempTurn);
				final String yesOption = messages.getString("optionYes");
				final String noOption = messages.getString("optionNo");
				final String invalidChoiceMessage =
					messages.getString("invalidChoice");
				final String playedAnotherAttack =
					messages.getString("playedAnotherAttack");
				final String noAnotherAttack =
					messages.getString("noAnotherAttack");

				System.out.println(attackCardPrompt);
				System.out.println(yesOption);
				System.out.println(noOption);

				String userInput = scanner.nextLine().trim();
				switch (userInput) {
					case "1":
						game.removeCardFromHand(tempTurn, CardType.ATTACK);
						if (checkAllPlayersForNope(tempTurn)) {
							continue;
						}
						game.addAttackQueue(normalAttack);
						tempTurn =
							(tempTurn + 1) % game.getNumberOfPlayers();
						System.out.println(playedAnotherAttack);
						break;
					case "2":
						System.out.println(noAnotherAttack);
						return;
					default:
						System.out.println(invalidChoiceMessage);
						break;
				}
			} else if (hasCard(tempTurn, CardType.TARGETED_ATTACK)) {
				final String hasTargetedAttackPrompt = MessageFormat.format(
					messages.getString
						("hasTargetedAttackPrompt"), tempTurn);
				final String yesOption = messages.getString("optionYes");
				final String noOption = messages.getString("optionNo");
				final String invalidChoiceMessage =
					messages.getString("invalidChoice");
				final String playedAnotherTargetedAttack =
					messages.getString("playedAnotherTargetedAttack");
				final String noAnotherTargetedAttack =
					messages.getString("noAnotherTargetedAttack");

				System.out.println(hasTargetedAttackPrompt);
				System.out.println(yesOption);
				System.out.println(noOption);

				String userInput = scanner.nextLine().trim();
				switch (userInput) {
					case "1":
						game.removeCardFromHand(tempTurn,
								CardType.TARGETED_ATTACK);
						if (checkAllPlayersForNope(tempTurn)) {
							continue;
						}
						int playerIndex = checkValidPlayerIndexInput();
						game.addAttackQueue(playerIndex);
						tempTurn = playerIndex;
						System.out.println(playedAnotherTargetedAttack);
						break;
					case "2":
						System.out.println(noAnotherTargetedAttack);
						return;
					default:
						System.out.println(invalidChoiceMessage);
						break;
				}
			}
		}
	}

	private void swapTopAndBottom() {
		final String playSwapCardMessage = messages.getString("swapTopAndBottomCardPlayed");
		System.out.println(playSwapCardMessage);
		game.swapTopAndBottom();
	}

	private void playAttack(boolean targeted) {
		final String playAttackCardMessage = messages.getString("attackCardPlayed");
		System.out.println(playAttackCardMessage);
		startAttackFollowUp(targeted);
		game.startAttackPhase();
	}


	private void playGarbageCollection() {
		Scanner scanner = new Scanner(System.in, StandardCharsets.UTF_8);

		final String garbageCollectionMessage =
				messages.getString("garbageCollectionPlayed");
		final String playerHandMessage = messages.getString("playerHandMessage");
		final String enterCardIndexMessage = messages.getString("enterCardIndex");
		final String invalidCardIndexMessage = messages.getString("invalidCardIndex");
		final String cursedPlayerMessage = messages.getString("cursedPlayerMessage");
		final String cursedCardPlaceholder = messages.getString("cursedCardPlaceholder");
		final String cursedPlayerShuffleMessage =
				messages.getString("cursedPlayerShuffleMessage");
		final String cardRemovedTemplate = messages.getString("cardRemovedFromHand");
		final String dividerLine = messages.getString("dividerLine");

		System.out.println(garbageCollectionMessage);
		System.out.println(dividerLine);

		for (int i = 0; i < game.getNumberOfPlayers(); i++) {
			Player player = game.getPlayerAtIndex(i);
			if (!player.getIsDead()) {

				if (checkIfPlayerIsCursed(player)) {
					final String cursedPlayerFormatted = MessageFormat.
							format(cursedPlayerMessage, i);
					System.out.print(cursedPlayerFormatted);

					for (int index = 0; index < player.getHandSize(); index++) {
						System.out.print(cursedCardPlaceholder);
					}
				} else {
					final String playerHandFormatted =
							MessageFormat.format(playerHandMessage, i);
					final StringBuilder handMessage =
							new StringBuilder(playerHandFormatted);
					for (int index = 0; index < player.getHandSize(); index++) {
						handMessage.append(" ").append(
								getLocalizedCardType(
								game.getCardType(i, index)));
					}
					System.out.println(handMessage);
				}
				System.out.println();

				int cardIndex = -1;
				boolean validIndex = false;
				while (!validIndex) {
					System.out.print(enterCardIndexMessage);
					cardIndex = scanner.nextInt();
					if (checkCardWithinBounds(cardIndex, player)) {
						validIndex = true;
					} else {
						System.out.println(invalidCardIndexMessage);
					}
				}

				Card cardToDiscard = player.getCardAt(cardIndex);
				player.removeCardFromHand(cardIndex);

				if (checkMatchingCardType(cardToDiscard.getCardType(),
						CardType.STREAKING_KITTEN)
						&& hasCard(i, CardType.EXPLODING_KITTEN)) {
					int explodingKittenIdx = game.getIndexOfCardFromHand(
							i, CardType.EXPLODING_KITTEN);

					player.removeCardFromHand(explodingKittenIdx);
					if (!playExplodingKitten(i)) {
						continue;
					}
				}

				if (player.getIsCursed()) {
					System.out.println(cursedPlayerShuffleMessage);
					for (int index = 0; index < player.getHandSize(); index++) {
						System.out.print
							(getLocalizedCardType
								(player.getCardAt(index).
									getCardType()));
					}
					System.out.println();
					player.shuffleHand();
				}

				game.playGarbageCollection(cardToDiscard.getCardType());

				final String cardRemovedMessage = MessageFormat.format
					(cardRemovedTemplate,
						getLocalizedCardType(cardToDiscard.getCardType()));
				System.out.println(cardRemovedMessage);
				System.out.println(dividerLine);
			}
		}
	}

	private void printPlayerTurn() {
		int currentPlayer = game.getPlayerTurn();
		final String dividerLine = messages.getString("dividerLine");
		final String currentPlayerTurnMessage;
		if (checkReversed()) {
			int reversedPlayer = game.getNumberOfPlayers() - currentPlayer - 1;
				currentPlayerTurnMessage = MessageFormat.format(
					messages.getString("currentPlayerTurnReversed"),
					reversedPlayer);
		} else {
			currentPlayerTurnMessage = MessageFormat.format(
					messages.getString("currentPlayerTurn"), currentPlayer);
		}

		Player player = game.getPlayerAtIndex(currentPlayer);

		System.out.println(dividerLine);

		if (checkIfPlayerIsCursed(player)) {
			final String cursedMessage = MessageFormat.format(
					messages.getString("cursedPlayer"), currentPlayer);
			System.out.println(cursedMessage);

			final StringBuilder handMessage = new StringBuilder
					(messages.getString("playerHand"));
			for (int handIndex = 0; handIndex <
					getHandSize(currentPlayer); handIndex++) {
				handMessage.append(" ").append(messages.getString("unknownCard"));
			}
			System.out.println(handMessage);
		} else {
			System.out.println(currentPlayerTurnMessage);
			final StringBuilder handMessage =
					new StringBuilder(messages.getString("playerHand"));
			for (int handIndex = 0;
				handIndex < getHandSize(currentPlayer); handIndex++) {
				handMessage.append(" ").append(
				getLocalizedCardType(
					game.getCardType(currentPlayer, handIndex)));
			}

			System.out.println(handMessage);
		}

		for (int playerIndex = 0; playerIndex < getNumberOfPlayers(); playerIndex++) {
			if (checkIfPlayerIsAlive(playerIndex)) {
				for (int cardIndex = 0;
					cardIndex < getHandSize(playerIndex); cardIndex++) {
					if (checkIfCardMarked(playerIndex, cardIndex)) {
						final String markedCardMessage =
							MessageFormat.format(
							messages.getString("markedCard"),
							playerIndex,
							cardIndex,
							game.getCardType
								(playerIndex,
									cardIndex));
						System.out.println(markedCardMessage);
					}
				}
			}
		}
	}

	private int playedCard() {
		Scanner scanner = new Scanner(System.in, StandardCharsets.UTF_8);
		final String promptMessage = messages.getString("playedCardPrompt");
		final String invalidIndexMessage = messages.getString("invalidIndex");
		final String invalidInputMessage = messages.getString("invalidNumber");
		int currentPlayer = game.getPlayerTurn();

		System.out.println(promptMessage);

		while (true) {
			String userInput = scanner.nextLine();
			try {
				int userIndex = Integer.parseInt(userInput);
				if (checkCardWithinBoundsIndexed(userIndex, currentPlayer)) {
					final String successMessage = MessageFormat.format(
							messages.getString("playedCardSuccess"),
							userIndex
					);
					System.out.println(successMessage);
					return userIndex;
				} else {
					final String formattedInvalidIndexMessage =
						MessageFormat.format(
						invalidIndexMessage,
						game.getHandSize(currentPlayer) - 1
						);
					System.out.println(formattedInvalidIndexMessage);
				}
			} catch (NumberFormatException e) {
				System.out.println(invalidInputMessage);
			}
		}
	}

	private int playSpecialCombo() {
		Scanner scanner = new Scanner(System.in, StandardCharsets.UTF_8);

		final String specialComboPrompt = messages.getString("specialComboPrompt");
		final String specialComboDecision = messages.getString("specialComboDecision");
		final String specialComboTwo = messages.getString("specialComboTwo");
		final String specialComboThree = messages.getString("specialComboThree");
		final String invalidInput = messages.getString("invalidInput");
		final String singleCardDecision = messages.getString("singleCardDecision");
		final String comboTypePrompt = messages.getString("comboTypePrompt");
		final String optionPrompt = messages.getString("optionsPrompt");
		final String optionYes = messages.getString("optionYes");
		final String optionNo = messages.getString("optionNo");

		System.out.println(specialComboPrompt);
		System.out.println(optionYes);
		System.out.println(optionNo);

		while (true) {
			System.out.print(optionPrompt);
			String userInput = scanner.nextLine();
			switch (userInput) {
				case "1":
					System.out.println(specialComboDecision);
					System.out.println(comboTypePrompt);
					while (true) {
						System.out.print(optionPrompt);
						final String userInputTwo = scanner.nextLine();
						final int twoCombo = 2;
						final int threeCombo = 3;
						switch (userInputTwo) {
							case "2":
								System.out.println(specialComboTwo);
								return twoCombo;
							case "3":
								System.out.println
									(specialComboThree);
								return threeCombo;
							default:
								System.out.println(invalidInput);
								break;
						}
					}
				case "2":
					System.out.println(singleCardDecision);
					return 0;
				default:
					System.out.println(invalidInput);
					break;
			}
		}
	}

	private void playNope(int playerIndex) {
		final String decidedToPlayNope = MessageFormat.format(
				messages.getString("decidedToPlayNope"), playerIndex);
		final String successfullyPlayedNope = MessageFormat.format(
				messages.getString("successfullyPlayedNope"), playerIndex);

		System.out.println(decidedToPlayNope);
		game.removeCardFromHand(playerIndex, CardType.NOPE);
		System.out.println(successfullyPlayedNope);
	}

	private boolean checkAllPlayersForNope(int playerIndex) {
		for (int playerCounter = 0;
			playerCounter < getNumberOfPlayers(); playerCounter++) {
			if (playerCounter != playerIndex) {
				if (game.checkIfPlayerHasCard(playerCounter, CardType.NOPE)) {
					final String hasNopeCard = MessageFormat.format(
						messages.getString
							("playerHasNopeCard"), playerCounter);
					final String wouldYouPlayNope = messages.getString
							("wouldYouPlayNope");
					final String optionYes = messages.getString("optionYes");
					final String optionNo = messages.getString("optionNo");
					final String invalidChoice = messages.getString
							("invalidChoiceForNope");

					System.out.println(hasNopeCard);
					System.out.println(wouldYouPlayNope);
					System.out.println(optionYes);
					System.out.println(optionNo);

					Scanner scanner = new Scanner(System.in,
							StandardCharsets.UTF_8);
					String userInput = scanner.nextLine();
					switch (userInput) {
						case "1":
							playNope(playerCounter);
							return !checkAllPlayersForNope
									(playerCounter);
						case "2":
							final String didNotPlayNope =
								MessageFormat.format
								(messages.getString
									("playerDidNotPlayNope"),
										playerCounter);
							System.out.println(didNotPlayNope);
							break;
						default:
							System.out.println(invalidChoice);
							playerCounter--;
							break;
					}
				}
			}
		}
		return false;
	}

	private boolean playExplodingKitten(int playerIndex) {
		Scanner scanner = new Scanner(System.in, StandardCharsets.UTF_8);

		final String explodingKittenMessage = messages.getString("explodingKittenMessage");
		final String noDefuseCardMessage = messages.getString("noDefuseCardMessage");
		final String youExplodedMessage = messages.getString("youExplodedMessage");
		final String defusedMessage = messages.getString("defusedMessage");
		final String whereToInsertMessage = messages.getString("whereToInsertMessage");
		final String validRangeMessage = MessageFormat.format(
		messages.getString("validRangeMessage"), game.getDeckSize());
		final String invalidInputMessage = messages.getString("invalidInputMessage");
		final String cursedMessage = messages.getString("cursedExplodingMessage");
		final String notDefuseCardMessage = messages.getString("notDefuseCardMessage");
		final String discardCardMessage = messages.getString("discardCardMessage");
		final String reenterDefuseMessage = messages.getString("reenterDefuseMessage");

		final String anotherExplodingKittenMessage =
				messages.getString("anotherExplodingKittenMessage");
		final String defusedFirstExplodingKitten =
				messages.getString("defusedFirstExplodingKitten");
		final String discardStreakingKittenMessage =
				messages.getString("discardStreakingKittenMessage");

		System.out.println(explodingKittenMessage);
		if (checkExplodingKitten(playerIndex)) {
			System.out.println(noDefuseCardMessage);
			System.out.println(youExplodedMessage);
			return false;
		} else {
			Player player = game.getPlayerAtIndex(playerIndex);
			if (player.getIsCursed()) {
				System.out.println(cursedMessage);

				while (true) {
					final String findDefuseCardMessage = MessageFormat.format(
							messages.getString("findDefuseCardMessage")
							, player.getHandSize() - 1);
					System.out.println(findDefuseCardMessage);
					String defuseString = scanner.nextLine();
					try {
					int defuseIndex = Integer.parseInt(defuseString);
					Card card = player.getCardAt(defuseIndex);
					if (checkMatchingCardType(card.getCardType(),
							CardType.DEFUSE)) {
						break;
					}
					else if (checkMatchingCardType(card.getCardType(),
					CardType.EXPLODING_KITTEN)) {
						System.out.println(anotherExplodingKittenMessage);
						player.removeCardFromHand(defuseIndex);
						boolean exploded = playExplodingKitten(playerIndex);
						if (!exploded) {
							return false;
						}
						System.out.println(defusedFirstExplodingKitten);
						if (!player.hasCard(CardType.DEFUSE)) {
							System.out.println(noDefuseCardMessage);
							System.out.println(youExplodedMessage);
							return false;
						}
					} else if (checkMatchingCardType(card.getCardType(),
						CardType.STREAKING_KITTEN)
						&& player.hasCard(CardType.EXPLODING_KITTEN)) {
						System.out.println(discardStreakingKittenMessage);
						player.removeCardFromHand(defuseIndex);
						player.removeCardFromHand(player.getIndexOfCard
								(CardType.EXPLODING_KITTEN));
						boolean exploded = playExplodingKitten(playerIndex);
						if (!exploded) {
							return false;
						}
						System.out.println(defusedFirstExplodingKitten);
						if (!player.hasCard(CardType.DEFUSE)) {
							System.out.println(noDefuseCardMessage);
							System.out.println(youExplodedMessage);
							return false;
						}

					} else {
						System.out.println(notDefuseCardMessage);
						player.removeCardFromHand(defuseIndex);
						System.out.println(discardCardMessage);
						System.out.println(reenterDefuseMessage);
					}

					} catch (Exception e) {
						System.out.println(invalidInputMessage);
					}
				}
			}

			System.out.println(defusedMessage);
			System.out.println(whereToInsertMessage);
			System.out.println(validRangeMessage);
			while (true) {
				String userInput = scanner.nextLine();
				try {
					int userIndex = Integer.parseInt(userInput);
					game.playDefuse(userIndex, playerIndex);
					game.getPlayerAtIndex(playerIndex).setCursed(false);
					return true;
				} catch (NumberFormatException e) {
					System.out.println(invalidInputMessage);
				} catch (UnsupportedOperationException e) {
					System.out.println(e.getMessage());
				}
			}
		}
	}

	private boolean endTurn() {
		Scanner scanner = new Scanner(System.in, StandardCharsets.UTF_8);

		Card cardDrawn = game.drawCard();
		if (checkIfNumberOfTurnsGreaterThanZero()) {
			game.decrementNumberOfTurns();
		}

		int currentPlayerIdx = game.getPlayerTurn();
		final String cardDrawnMessage = MessageFormat.format
				(messages.getString("cardDrawnMessage"),
						getLocalizedCardType(cardDrawn.getCardType()));
		final String streakingKittenMessage = messages.getString("streakingKittenMessage");
		final String invalidNumber = messages.getString("invalidNumber");
		if (checkMatchingCardType(cardDrawn.getCardType(), CardType.EXPLODING_KITTEN)) {
			System.out.println(cardDrawnMessage);
			if (hasCard(currentPlayerIdx, CardType.STREAKING_KITTEN) &&
					!(hasCard(currentPlayerIdx, CardType.EXPLODING_KITTEN))) {
				System.out.println(streakingKittenMessage);
				while (true) {
					String keepExplodingKitten = scanner.nextLine();
					try {
						int keepExplodingKittenInput =
							Integer.parseInt
								(keepExplodingKitten);
						if (keepExplodingKittenInput == 1) {
							game.addCardToHand(cardDrawn);
							return true;
						} else {
							break;
						}
					} catch (NumberFormatException e) {
						System.out.println(invalidNumber);
					}
				}
			}
			return playExplodingKitten(currentPlayerIdx);
		} else if (checkMatchingCardType(cardDrawn.getCardType(),
				CardType.IMPLODING_KITTEN)) {
			return playImplodingKitten(cardDrawn);
		} else {
			System.out.println(cardDrawnMessage);
			game.addCardToHand(cardDrawn);
			game.getPlayerAtIndex(currentPlayerIdx).setCursed(false);
			return true;
		}
	}

	private boolean checkIfTheyEndTurn() {
		Scanner scanner = new Scanner(System.in, StandardCharsets.UTF_8);
		final String endTurnPrompt = messages.getString("endTurnPrompt");
		final String typeOptionPrompt = messages.getString("typeOptionPrompt");
		final String endTurnConfirmed = messages.getString("endTurnConfirmed");
		final String playCardOrCombo = messages.getString("playCardOrCombo");
		final String optionYes = messages.getString("optionYes");
		final String optionNo = messages.getString("optionNo");

		System.out.println(endTurnPrompt);
		System.out.println(optionYes);
		System.out.println(optionNo);

		while (true) {
			System.out.println(typeOptionPrompt);
			String userInput = scanner.nextLine();
			switch (userInput) {
				case "1":
					System.out.println(endTurnConfirmed);
					boolean isPlayerAlive = endTurn();
					if (!isPlayerAlive || checkIfNumberOfTurnsIsZero()) {
						game.incrementPlayerTurn();
					}
					return true;
				case "2":
					System.out.println(playCardOrCombo);
					return false;
				default:
					break;
			}
		}
	}

	private void playCurseOfTheCatButt() {
		final String decidedToPlayCard = messages.getString("decidedToPlayCatButtCard");
		final String enterPlayerIndex = messages.getString("enterPlayerIndexCurse");
		final String invalidPlayerIndex = messages.getString("invalidPlayerIndex");
		final String playerExploded = messages.getString("playerExplodedChooseNew");
		final String cannotCurseSelf = messages.getString("cannotCurseSelf");
		final String enterValidInteger = messages.getString("enterValidInteger");
		final String playerCursed = messages.getString("playerCursed");

		System.out.println(decidedToPlayCard);

		Scanner scanner = new Scanner(System.in, StandardCharsets.UTF_8);
		int playerIndex;

		while (true) {

			System.out.print(enterPlayerIndex);
			try {
				playerIndex = scanner.nextInt();
				if (checkUserOutOfBounds(playerIndex)) {
					final String invalidPlayerFormatted = MessageFormat.
							format(invalidPlayerIndex, playerIndex);
					System.out.println(invalidPlayerFormatted);
				} else if (!checkIfPlayerIsAlive(playerIndex)) {
					System.out.println(playerExploded);
				} else if (checkIfCurrentPlayerTurn(playerIndex)) {
					System.out.println(cannotCurseSelf);
				} else {
					break;
				}
			} catch (Exception e) {
				System.out.println(enterValidInteger);
				scanner.next();
			}
		}

		game.getPlayerAtIndex(playerIndex).setCursed(true);
		game.getPlayerAtIndex(playerIndex).shuffleHand();

		final String formattedPlayerCursed =
			MessageFormat.format(playerCursed, playerIndex);
		System.out.println(formattedPlayerCursed);
	}

	private void playSpecialComboTwoCards(CardType cardType) {
		Scanner scanner = new Scanner(System.in, StandardCharsets.UTF_8);

		final String decidedToPlayCombo = messages.getString("decidedToPlayCombo");
		final String enterPlayerToSteal = messages.getString("enterPlayerToSteal");
		final String enterValidPlayerIndex = messages.getString("enterValidPlayerIndex");
		final String playerExploded = messages.getString("playerExploded");
		final String cannotStealFromSelf = messages.getString("cannotStealFromSelf");
		final String playerNoCardsToSteal = messages.getString("playerNoCardsToSteal");
		final String successfullyPlayedCombo =
			messages.getString("successfullyPlayedCombo");
		final String enterIntegerMessage = messages.getString("enterInteger");
		final String streakingKittenMessage = messages.getString("streakingKittenMessage");
		final String invalidNumber = messages.getString("invalidNumber");

		System.out.println(decidedToPlayCombo);


		int playerIndex;

		while (true) {
			System.out.print(enterPlayerToSteal);
			try {
				playerIndex = scanner.nextInt();
				if (checkUserOutOfBounds(playerIndex)) {
					final String validPlayerIndexMessage =
						enterValidPlayerIndex +
						(game.getNumberOfPlayers() - 1);
					System.out.println(validPlayerIndexMessage);
				} else if (!checkIfPlayerIsAlive(playerIndex)) {
					System.out.println(playerExploded);
				} else if (checkIfCurrentPlayerTurn(playerIndex)) {
					System.out.println(cannotStealFromSelf);
				} else {
					break;
				}
			} catch (Exception e) {
				System.out.println(enterIntegerMessage);
				scanner.next();
			}
		}

		try {
			Card stealedCard = game.stealRandomCard(playerIndex);
			if (checkMatchingCardType(stealedCard.getCardType(),
					CardType.EXPLODING_KITTEN)) {
				Player currentPlayer = game.getPlayerAtIndex(game.getPlayerTurn());
				if (currentPlayer.hasCard(CardType.STREAKING_KITTEN) &&
					!currentPlayer.hasCard(CardType.EXPLODING_KITTEN)) {
					System.out.println(streakingKittenMessage);
					while (true) {
					String keepExplodingKitten = scanner.nextLine();
					try {
						int keepExplodingKittenInput =
						Integer.parseInt(keepExplodingKitten);
						if (keepExplodingKittenInput == 1) {
							break;
						} else {
						int explodingKittenIndex =
						game.getIndexOfCardFromHand(game.getPlayerTurn()
						, CardType.EXPLODING_KITTEN);
						game.getPlayerAtIndex(game.getPlayerTurn())
						.removeCardFromHand(explodingKittenIndex);
						playExplodingKitten(game.getPlayerTurn());
						}
					} catch (NumberFormatException e) {
						System.out.println(invalidNumber);
					}
					}
				} else {
					int explodingKittenIndex =
						game.getIndexOfCardFromHand(game.getPlayerTurn()
						, CardType.EXPLODING_KITTEN);
					game.getPlayerAtIndex(game.getPlayerTurn())
							.removeCardFromHand(explodingKittenIndex);
					playExplodingKitten(game.getPlayerTurn());
				}

			} else if (checkMatchingCardType(stealedCard.getCardType(),
					CardType.STREAKING_KITTEN)
			&& hasCard(playerIndex, CardType.EXPLODING_KITTEN)) {
				int explodingKittenIndex = game.getIndexOfCardFromHand(playerIndex
						, CardType.EXPLODING_KITTEN);
				game.getPlayerAtIndex(playerIndex).
						removeCardFromHand(explodingKittenIndex);
				playExplodingKitten(playerIndex);
			}
		} catch (IllegalArgumentException e) {
			System.out.println(playerNoCardsToSteal);
			return;
		}

		System.out.println(successfullyPlayedCombo);
	}

	private void drawFromTheBottom() {
		final String drewFromBottomMessage = messages.getString("drewFromBottom");
		final String cardWasMessage = messages.getString("cardWas");

		System.out.println(drewFromBottomMessage);
		game.addCardToHand(game.drawFromBottom());

		final String cardType =
				game.getCardType(game.getPlayerTurn(),
					game.getHandSize(game.
							getPlayerTurn()) - 1)
				.toString();

		final String formattedCardMessage = MessageFormat.format(cardWasMessage, cardType);
		System.out.println(formattedCardMessage);
	}

	private void playMark() {
		final String decidedToPlayMark = messages.getString("decidedToPlayMark");
		final String enterPlayerToMark = messages.getString("enterPlayerToMark");
		final String invalidPlayerRange = messages.getString
				("invalidPlayerRange") + (game.getNumberOfPlayers() - 1);
		final String playerExploded = messages.getString("playerExploded");
		final String cannotMarkSelf = messages.getString("cannotMarkSelf");
		final String enterCardToMark = messages.getString("enterCardToMark");
		final String enterInteger = messages.getString("enterInteger");

		System.out.println(decidedToPlayMark);

		Scanner scanner = new Scanner(System.in, StandardCharsets.UTF_8);
		int playerIndex;
		int cardIndex;

		while (true) {
			System.out.print(enterPlayerToMark);
			try {
				playerIndex = scanner.nextInt();
				if (checkUserOutOfBounds(playerIndex)) {
					System.out.println(invalidPlayerRange);
				} else if (!checkIfPlayerIsAlive(playerIndex)) {
					System.out.println(playerExploded);
				} else if (checkIfCurrentPlayerTurn(playerIndex)) {
					System.out.println(cannotMarkSelf);
				} else {
					break;
				}
			} catch (Exception e) {
				System.out.println(enterInteger);
				scanner.next();
			}
		}

		while (true) {
			final String invalidCardRange = messages.getString("invalidCardRange")
					+ (game.getHandSize(playerIndex) - 1);
			System.out.print(enterCardToMark);
			try {
				cardIndex = scanner.nextInt();
				if (checkCardWithinBoundsIndexed(cardIndex, playerIndex)) {
					break;
				} else {
					System.out.println(invalidCardRange);
				}
			} catch (Exception e) {
				System.out.println(enterInteger);
				scanner.next();
			}
		}

		game.playMark(playerIndex, cardIndex);
		final String cardMarked = MessageFormat.format
				(messages.getString("cardMarked"), cardIndex, playerIndex);
		System.out.println(cardMarked);
	}


	private void playCatomicBomb() {
		final int explodingKittenAtTop = 0;
		final int explodingKittenSecondFromTop = 1;
		final int explodingKittenThirdFromTop = 2;
		final int explodingKittenFourthFromTop = 3;
		final String playCatomicBomb = messages.getString("playCatomicBomb");
		final String explodingKittensTop = messages.getString("explodingKittensTop");
		System.out.println(playCatomicBomb);
		System.out.println(explodingKittensTop);
		game.playCatomicBomb();

		for (int index = 0; index < getDeckSize(); index++) {
			if (checkMatchingCardType(game.getDeckCardType(
					getDeckSize() - index - 1),
					CardType.EXPLODING_KITTEN)) {
				final String formattedMessage;
				switch (index) {
					case explodingKittenAtTop:
						formattedMessage = MessageFormat.format(
								messages.getString(
								"cardExplodingKittenTh"),
								index);
						break;
					case explodingKittenSecondFromTop:
						formattedMessage = MessageFormat.format(
								messages.getString(
									"cardExplodingKittenSt"),
											index);
						break;
					case explodingKittenThirdFromTop:
						formattedMessage = MessageFormat.format(
								messages.getString(
									"cardExplodingKittenNd"),
											index);
						break;
					case explodingKittenFourthFromTop:
						formattedMessage = MessageFormat.format(
								messages.getString(
									"cardExplodingKittenRd"),
											index);
						break;
					default:
						formattedMessage = "";
				}
				if (!formattedMessage.isEmpty()) {
					System.out.println(formattedMessage);
				}
			}
		}
	}

	private void playReverse() {
		final String reverseMessage = messages.getString("reverseMessage");
		System.out.println(reverseMessage);
		game.playReverse();
	}

	private void playSpecialComboThreeCards(CardType cardType) {
		Scanner scanner = new Scanner(System.in, StandardCharsets.UTF_8);

		final String enterValidInteger = messages.getString("enterValidInteger");
		final String enterPlayerToSteal = messages.getString("enterPlayerToSteal");
		final String invalidPlayerIndex = messages.getString("invalidPlayerIndex");
		final String playerExploded = messages.getString("playerExploded");
		final String cannotStealFromSelf = messages.getString("cannotStealFromSelf");
		final String enterCardTypeToSteal = messages.getString("enterCardTypeToSteal");
		final String selectedCardType = messages.getString("selectedCardType");
		final String invalidCardType = messages.getString("invalidCardType");
		final String playerHasNoCardsToSteal =
				messages.getString("playerHasNoCardsToSteal");
		final String successfullyPlayedCombo =
				messages.getString("successfullyPlayedThreeCombo");

		int playerIndex;

		while (true) {
			System.out.print(enterPlayerToSteal);
			try {
				playerIndex = scanner.nextInt();
				if (checkUserOutOfBounds(playerIndex)) {
					final String invalidPlayerFormatted = MessageFormat.
							format(invalidPlayerIndex, playerIndex);
					System.out.println(invalidPlayerFormatted);
				} else if (!checkIfPlayerIsAlive(playerIndex)) {
					System.out.println(playerExploded);
				} else if (checkIfCurrentPlayerTurn(playerIndex)) {
					System.out.println(cannotStealFromSelf);
				} else {
					break;
				}
			} catch (Exception e) {
				System.out.println(enterValidInteger);
				scanner.next();
			}
		}

		CardType cardTypeToSteal;
		while (true) {
			System.out.println(enterCardTypeToSteal);
			final String cardTypeString = scanner.next().toUpperCase();
			try {
				cardTypeToSteal = CardType.valueOf(cardTypeString);
				System.out.println(MessageFormat.format
						(selectedCardType, cardTypeToSteal));
				break;
			} catch (IllegalArgumentException e) {
				System.out.println(invalidCardType);
			}
		}

		try {
			game.stealTypeCard(cardTypeToSteal, playerIndex);
		} catch (IllegalArgumentException e) {
			System.out.println(playerHasNoCardsToSteal);
			return;
		}

		System.out.println(successfullyPlayedCombo);
	}

	private void playSeeTheFuture() {
		final int cardsSeenStreaking = 5;
		final int cardsSeenExploding = 2;
		final String decidedSeeFuture = messages.getString("decidedSeeFuture");
		final String futureCards = messages.getString("futureCards");

		System.out.println(decidedSeeFuture);
		int deckSize = getDeckSize();
		int cardsToReveal = (matchingGameType(GameType.STREAKING_KITTENS))
				? cardsSeenStreaking : cardsSeenExploding;
		cardsToReveal = Math.min(cardsToReveal, deckSize);

		final String futureCardsMessage = MessageFormat.format(futureCards, cardsToReveal);
		System.out.println(futureCardsMessage);

		for (int i = 0; i < cardsToReveal; i++) {
			Card card = game.getCardAtIndex(deckSize - 1 - i);
			final String cardMessage = MessageFormat.format
					(messages.getString("futureCard"),
							getLocalizedCardType(card.getCardType()));
			System.out.println(cardMessage);
		}
	}

	private void playShuffle() {
		final String decidedShuffle = messages.getString("decidedShuffle");
		final String enterShuffleTimes = messages.getString("enterShuffleTimes");
		final String enterPositiveInteger = messages.getString("enterPositiveInteger");
		final String enterInteger = messages.getString("enterInteger");

		System.out.println(decidedShuffle);

		Scanner scanner = new Scanner(System.in, StandardCharsets.UTF_8);
		int numberOfShuffle;
		final int maxNumberOfShuffles = 100;
		while (true) {
			System.out.print(enterShuffleTimes);
			try {
				numberOfShuffle = scanner.nextInt();
				if (numberOfShuffle > maxNumberOfShuffles) {
					final String maxShuffleMessage =
							messages.getString("maxShuffleMessage");
					System.out.println(maxShuffleMessage);
				}
				else if (numberOfShuffle > 0) {
					break;
				} else {
					System.out.println(enterPositiveInteger);
				}
			} catch (Exception e) {
				System.out.println(enterInteger);
				scanner.next();
			}
		}
		game.playShuffle(numberOfShuffle);
	}

	private void playSkip(boolean superSkip) {
		final String decidedSkip = messages.getString("decidedSkip");
		final String turnsRemainingTemplate = messages.getString("turnsRemaining");
		final String formattedTurnsRemaining;

		System.out.println(decidedSkip);
		int turnsLeft = game.playSkip(superSkip);
		formattedTurnsRemaining = MessageFormat.format(turnsRemainingTemplate, turnsLeft);
		System.out.println(formattedTurnsRemaining);
	}

	private void playAlterTheFuture() {
		final String alterFutureMessage = messages.getString("alterFutureMessage");
		final String futureCardsMessage = messages.getString("futureCardsMessage");
		final String enterNewOrderMessage = messages.getString("enterNewOrderMessage");
		final String positionPrompt = messages.getString("positionPrompt");
		final String positionTakenMessage = messages.getString("positionTakenMessage");
		final String invalidPositionMessage = messages.getString("invalidPositionMessage");
		final String futureAlteredMessage = messages.getString("futureAlteredMessage");
		final String cardDisplayFormat = messages.getString("cardDisplayFormat");

		System.out.println(alterFutureMessage);
		final int cardsAlteredStreaking = 5;
		final int cardsAlteredExploding = 3;
		int deckSize = game.getDeck().getDeckSize();
		int cardsToReveal = (matchingGameType(GameType.STREAKING_KITTENS))
				? cardsAlteredStreaking : cardsAlteredExploding;
		cardsToReveal = Math.min(cardsToReveal, deckSize);

		final String futureMessage =
			MessageFormat.format(futureCardsMessage, cardsToReveal);
		System.out.println(futureMessage);

		List<Card> revealedCards = new ArrayList<>();
		for (int i = 0; i < cardsToReveal; i++) {
			Card card = game.getDeck().getCardAtIndex(deckSize - 1 - i);
			revealedCards.add(card);
			final String cardDisplay = MessageFormat.format
					(cardDisplayFormat, i + 1,
							getLocalizedCardType(card.getCardType()));
			System.out.println(cardDisplay);
		}

		final String orderPrompt =
			MessageFormat.format(enterNewOrderMessage, cardsToReveal);
		System.out.println(orderPrompt);

		int[] newOrder = new int[cardsToReveal];
		Scanner scanner = new Scanner(System.in, StandardCharsets.UTF_8);

		for (int i = 0; i < cardsToReveal; i++) {
			final String positionRequest = MessageFormat.format(positionPrompt, i + 1);
			System.out.print(positionRequest);
			int inputPosition = scanner.nextInt();

			if (Arrays.stream(newOrder).anyMatch(pos ->
					(deckSize - pos) == inputPosition)) {
				final String positionTaken = MessageFormat.format
						(positionTakenMessage, inputPosition);
				System.out.println(positionTaken);
				i--;
				continue;
			}

			if (inputPosition < 1 || inputPosition > cardsToReveal) {
				final String invalidPosition = MessageFormat.format
						(invalidPositionMessage, cardsToReveal);
				System.out.println(invalidPosition);
				i--;
				continue;
			}
			newOrder[i] = deckSize - inputPosition;
		}

		game.getDeck().reorderDeckCards(newOrder, revealedCards);
		System.out.println(futureAlteredMessage);
	}

	private boolean checkIfCatCard(CardType cardType) {
		return cardType == CardType.CAT_ONE || cardType == CardType.CAT_TWO
				|| cardType == CardType.CAT_THREE || cardType == CardType.CAT_FOUR;
	}

	private int checkNumberOfFeralCats(int playerIndex) {
		return game.getPlayerAtIndex(playerIndex).
				checkNumberOfCardsInHand(CardType.FERAL_CAT);
	}


	private void promptCursedMessage() {
		final String cursedMessage = messages.getString("cursedMessage");
		System.out.println(cursedMessage);
	}

	private boolean checkCardIfInvalid(Card card) {
		CardType[] inValidCards = {CardType.NOPE,
				CardType.DEFUSE, CardType.EXPLODING_KITTEN,
				CardType.STREAKING_KITTEN,
				CardType.IMPLODING_KITTEN, CardType.FERAL_CAT};
		for (CardType inValidCard : inValidCards) {
			if (checkMatchingCardType(card.getCardType(), inValidCard)) {
				return true;
			}
		}
		return false;
	}

	private boolean playImplodingKitten(Card card) {
		final String youExplodedMessage = messages.getString("youExplodedMessage");
		final String enterIndexMessage = MessageFormat.format
				(messages.getString("enterImplodingKittenIndex"),
						game.getDeck().getDeckSize());
		final String invalidNegativeIndexMessage =
				messages.getString("invalidNegativeIndex");
		final String invalidGreaterIndexMessage = messages.getString("invalidGreaterIndex");

		if (card.checkIfFacedUp()) {
			game.playImplodingKitten();
			System.out.println(youExplodedMessage);
			return false;
		}

		card.setFacedUp();
		Scanner scanner = new Scanner(System.in, StandardCharsets.UTF_8);

		while (true) {
			System.out.println(enterIndexMessage);
			int indexToInsert = scanner.nextInt();

			if (checkNegativeIndexDeck(indexToInsert)) {
				System.out.println(invalidNegativeIndexMessage);
			} else if (checkIfGreaterThanMaxIndexDeck(indexToInsert)) {
				System.out.println(invalidGreaterIndexMessage);
			} else {
				game.getDeck().insertImplodingKittenAtIndex(indexToInsert, card);
				break;
			}
		}
		return true;
	}

	public void startTurn() {
		if (checkIfNumberOfTurnsIsZero()) {
			game.setTurnToTargetedIndexIfAttackOccurred();
			game.setPlayerNumberOfTurns();
		}
		printPlayerTurn();
		Scanner scanner = new Scanner(System.in, StandardCharsets.UTF_8);
		final String playerTurnsMessage = MessageFormat.format
				(messages.getString("playerTurnsMessage"), game.getNumberOfTurns());
		final String enterValidIntegerMessage = messages.getString("enterValidInteger");
		final String notEnoughCardsComboMessage = messages.getString
				("notEnoughCardsComboMessage");
		final String notEnoughCardsMessage = messages.getString("notEnoughCardsMessage");
		final String howManyFeralCatsMessage = messages.getString("howManyFeralCats");
		final String invalidFeralCatsNumberMessage =
				messages.getString("invalidFeralCatsNumber");
		final String invalidCardTypeMessage = messages.getString("invalidCardTypeMessage");

		System.out.println(playerTurnsMessage);

		while (!checkIfTheyEndTurn()) {
			printPlayerTurn();
			int specialCombo = playSpecialCombo();
			int cardIndex = playedCard();
			int playerIndex = game.getPlayerTurn();
			Player player = game.getPlayerAtIndex(playerIndex);
			CardType cardType = game.getCardType(playerIndex, cardIndex);

			if (checkIfPlayerIsCursed(player) && checkMatchingCardType(cardType,
					CardType.EXPLODING_KITTEN)) {
				player.removeCardFromHand(cardIndex);
				playExplodingKitten(playerIndex);
				continue;
			}

			if (checkIfPlayerIsCursed(player) && checkMatchingCardType(cardType,
					CardType.STREAKING_KITTEN)) {
				if (hasCardDirect(player, CardType.EXPLODING_KITTEN)) {
					player.removeCardFromHand(cardIndex);
					int explodingKittenIdx = game.getIndexOfCardFromHand
							(playerIndex, CardType.EXPLODING_KITTEN);
					player.removeCardFromHand(explodingKittenIdx);
					playExplodingKitten(playerIndex);
					continue;
				}
			}

			if (checkIfPlayerIsCursed(player) && specialCombo == 0) {
				Card card = player.getCardAt(cardIndex);
				if (checkCardIfInvalid(card)) {
					promptCursedMessage();
					player.removeCardFromHand(cardIndex);
					continue;
				}
			}
			if (specialCombo == 0 && checkCardIfInvalid(player.getCardAt(cardIndex))) {
				System.out.println(invalidCardTypeMessage);
				continue;
			}

			final int threeCats = 3;
			final int twoCats = 2;

			if (specialCombo == threeCats) {
				int numberOfFeralCats =
						checkNumberOfFeralCats(game.getPlayerTurn());
				if (checkIfCatCard(cardType) && numberOfFeralCats > 0) {
					int numberOfCatType =
							player.checkNumberOfCardsInHand(cardType);
					if (numberOfFeralCats + numberOfCatType >= threeCats) {
						System.out.println(howManyFeralCatsMessage);
						int numberOfFeralCatsToPlay = 0;
						try {
							numberOfFeralCatsToPlay = scanner.nextInt();

							while (checkFeralCat(numberOfFeralCatsToPlay
									, numberOfFeralCats,
									numberOfCatType,
									threeCats)) {
								System.out.println
								(invalidFeralCatsNumberMessage);
								System.out.println
								(howManyFeralCatsMessage);
								numberOfFeralCatsToPlay =
									scanner.nextInt();
							}
						} catch (Exception e) {
							System.out.println
									(enterValidIntegerMessage);
							scanner.next();
						}
						for (int i = 0; i < numberOfFeralCatsToPlay; i++) {
							player.removeCardFromHand
								(player.getIndexOfCard
									(CardType.FERAL_CAT));
						}
						for (int i = 0;
							i < threeCats - numberOfFeralCatsToPlay;
							i++) {
							player.removeCardFromHand
								(player.getIndexOfCard(cardType));
						}
					} else {
						if (checkIfPlayerIsCursed(player)) {
							promptCursedMessage();
							player.removeCardFromHand(cardIndex);
							continue;
						}
						System.out.println(notEnoughCardsComboMessage);
						continue;
					}
				} else {
					if (checkNumberOfCards(player, cardType, threeCats)) {
						for (int i = 0; i < threeCats; i++) {
							player.removeCardFromHand
								(player.getIndexOfCard(cardType));
						}
					} else {
						if (checkIfPlayerIsCursed(player)) {
							promptCursedMessage();
							player.removeCardFromHand(cardIndex);
							continue;
						}
						System.out.println(notEnoughCardsComboMessage);
						continue;
					}
				}

			} else if (checkIfCatCard(cardType) || specialCombo == twoCats) {
				boolean catCard = checkIfCatCard(cardType);
				int numberOfFeralCats = checkNumberOfFeralCats
					(game.getPlayerTurn());
				if (catCard && numberOfFeralCats > 0) {
					int numberOfCatType =
						player.checkNumberOfCardsInHand(cardType);
					int numberOfFeralCatsToPlay = 0;
					if (numberOfFeralCats + numberOfCatType >= twoCats) {
						System.out.println(howManyFeralCatsMessage);
						try {
							numberOfFeralCatsToPlay = scanner.nextInt();
							while (checkFeralCat(numberOfFeralCatsToPlay
							, numberOfFeralCats,
									numberOfCatType, twoCats)) {

								System.out.println
								(invalidFeralCatsNumberMessage);
								System.out.println
								(howManyFeralCatsMessage);
								numberOfFeralCatsToPlay =
									scanner.nextInt();
							}
						} catch (Exception e) {
							System.out.println
								(enterValidIntegerMessage);
							scanner.next();
						}
						for (int i = 0; i < numberOfFeralCatsToPlay; i++) {
							player.removeCardFromHand
								(player.getIndexOfCard
									(CardType.FERAL_CAT));
						}
						for (int i = 0; i < twoCats -
								numberOfFeralCatsToPlay; i++) {
							player.removeCardFromHand
								(player.getIndexOfCard(cardType));
						}
					} else {
						if (player.getIsCursed()) {
							promptCursedMessage();
							player.removeCardFromHand(cardIndex);
							continue;
						}
						System.out.println
							(notEnoughCardsComboMessage);
						continue;
					}
				} else {

					if (checkNumberOfCards(player, cardType, twoCats)) {
						player.removeCardFromHand(
								player.getIndexOfCard(cardType));
						player.removeCardFromHand(
								player.getIndexOfCard(cardType));
					} else {
						if (checkIfPlayerIsCursed(player)) {
							promptCursedMessage();
							player.removeCardFromHand(cardIndex);
							continue;
						}
						System.out.println(notEnoughCardsMessage);
						continue;
					}
				}
			} else {
				player.removeCardFromHand(cardIndex);
			}

			if (checkIfDifferentCardType(cardType, CardType.EXPLODING_KITTEN)
					&& checkIfDifferentCardType(cardType, CardType.DEFUSE)) {
				if (checkAllPlayersNope()) {
					continue;
				}
			} else if (specialCombo == twoCats || specialCombo == threeCats) {
				if (checkAllPlayersNope()) {
					continue;
				}
			}


			if (specialCombo ==  twoCats) {
				playSpecialComboTwoCards(cardType);
				continue;
			} else if (specialCombo == threeCats) {
				playSpecialComboThreeCards(cardType);
				continue;
			}

			switch (cardType) {
				case ATTACK:
					playAttack(false);
					return;
				case TARGETED_ATTACK:
					playAttack(true);
					return;
				case SWAP_TOP_AND_BOTTOM:
					final String topCardMessage = MessageFormat.format(
						messages.getString("swapTopMessage"),
							getLocalizedCardType(
							game.getDeck().getCardAtIndex
							(game.getDeckSize() - 1).getCardType())
					);
					final String bottomCardMessage = MessageFormat.format(
						messages.getString("swapBottomMessage"),
							getLocalizedCardType(
							game.getDeck().getCardAtIndex(0)
									.getCardType())
					);
					System.out.println(topCardMessage);
					System.out.println(bottomCardMessage);

					swapTopAndBottom();

					final String newTopCardMessage = MessageFormat.format(
						messages.getString("newSwapTopMessage"),
							getLocalizedCardType(
							game.getDeck().getCardAtIndex
							(game.getDeckSize() - 1).getCardType())
					);
					final String newBottomCardMessage = MessageFormat.format(
							messages.getString("newSwapBottomMessage"),
							getLocalizedCardType(
							game.getDeck().getCardAtIndex(0)
									.getCardType())
					);
					System.out.println(newTopCardMessage);
					System.out.println(newBottomCardMessage);
					break;
				case SHUFFLE:
					playShuffle();
					break;
				case SKIP:
					playSkip(false);
					return;
				case SUPER_SKIP:
					playSkip(true);
					return;
				case CAT_ONE:
					playSpecialComboTwoCards(CardType.CAT_ONE);
					break;
				case CAT_TWO:
					playSpecialComboTwoCards(CardType.CAT_TWO);
					break;
				case CAT_THREE:
					playSpecialComboTwoCards(CardType.CAT_THREE);
					break;
				case CAT_FOUR:
					playSpecialComboTwoCards(CardType.CAT_FOUR);
					break;
				case DRAW_FROM_THE_BOTTOM:
					drawFromTheBottom();
					break;
				case CATOMIC_BOMB:
					playCatomicBomb();
					return;
				case REVERSE:
					playReverse();
					return;
				case SEE_THE_FUTURE:
					playSeeTheFuture();
					break;
				case GARBAGE_COLLECTION:
					playGarbageCollection();
					break;
				case MARK:
					playMark();
					break;
				case ALTER_THE_FUTURE:
					playAlterTheFuture();
					break;
				case CURSE_OF_THE_CAT_BUTT:
					playCurseOfTheCatButt();
					break;
				default:
					break;
			}
		}
	}

	public void endGame() {
		final String gameOverMessage = messages.getString("gameOverMessage");
		System.out.println(gameOverMessage);
	}

	public boolean checkIfGameOver() {
		return game.checkNumberOfAlivePlayers() == 1;
	}

	private boolean checkUserWithinBounds(int userIndex) {
		return userIndex >= 0 && userIndex < game.getNumberOfPlayers();
	}

	private boolean checkUserOutOfBounds(int userIndex) {
		return userIndex < 0 || userIndex >= game.getNumberOfPlayers();
	}

	private boolean checkIfPlayerIsAlive(int userIndex) {
		return !game.checkIfPlayerDead(userIndex);
	}

	private boolean hasCard(int userIndex, CardType cardType) {
		return game.checkIfPlayerHasCard(userIndex, cardType);
	}

	private boolean hasCardDirect(Player player, CardType cardType) {
		return player.hasCard(cardType);
	}

	private boolean checkCardWithinBounds(int cardIndex, Player player) {
		return cardIndex >= 0 && cardIndex < player.getHandSize();
	}

	private boolean checkCardWithinBoundsIndexed(int cardIndex, int playerIndex) {
		return cardIndex >= 0
				&& cardIndex < game.getHandSize(playerIndex);
	}

	private boolean checkMatchingCardType(CardType cardType, CardType cardTypeTwo) {
		return cardType == cardTypeTwo;
	}

	private boolean checkIfPlayerIsCursed(Player player) {
		return player.getIsCursed();
	}

	private boolean checkReversed() {
		return game.getIsReversed();
	}

	private int getHandSize(int playerIndex) {
		return game.getHandSize(playerIndex);
	}

	private boolean checkIfCardMarked(int playerIndex, int cardIndex) {
		return game.getPlayerAtIndex(playerIndex)
				.getCardAt(cardIndex).checkIfMarked();
	}

	private int getNumberOfPlayers() {
		return game.getNumberOfPlayers();
	}

	private boolean checkExplodingKitten(int playerIndex) {
		final String invalidPlayerIndexExplodingKitten = messages.
				getString("invalidPlayerIndexExplodingKitten");
		boolean isPlayerExploded = false;
		try {
			isPlayerExploded = game.playExplodingKitten(playerIndex);
		} catch (UnsupportedOperationException e) {
			System.out.println(invalidPlayerIndexExplodingKitten);
		}
		return isPlayerExploded;
	}

	private boolean checkIfNumberOfTurnsGreaterThanZero() {
		return game.getNumberOfTurns() > 0;
	}

	private boolean checkIfNumberOfTurnsIsZero() {
		return game.getNumberOfTurns() == 0;
	}

	private boolean checkIfCurrentPlayerTurn(int playerIndex) {
		return playerIndex == game.getPlayerTurn();
	}

	private int getDeckSize() {
		return game.getDeckSize();
	}

	private boolean matchingGameType (GameType gameType) {
		return gameType == game.getGameType();
	}

	private boolean checkFeralCat(int numberOfFeralCatsToPlay,
					int numberOfFeralCats, int numberOfCatType, int threshold) {
		return numberOfFeralCatsToPlay > numberOfFeralCats ||
				numberOfFeralCatsToPlay + numberOfCatType < threshold;
	}

	private boolean checkIfDifferentCardType(CardType cardType, CardType cardTypeTwo) {
		return cardType != cardTypeTwo;
	}

	private boolean checkAllPlayersNope() {
		return checkAllPlayersForNope(game.getPlayerTurn());
	}

	private boolean checkNumberOfCards(Player player, CardType cardtype, int threshold) {
		return player.checkNumberOfCardsInHand(cardtype) >= threshold;
	}

	private String getLocalizedCardType(CardType cardType) {
		String cardTypeKey = "card." + cardType.name();
		return messages.getString(cardTypeKey);
	}

	private boolean checkNegativeIndexDeck(int indexToInsert) {
		return indexToInsert < 0;
	}

	private boolean checkIfGreaterThanMaxIndexDeck(int indexToInsert) {
		return indexToInsert > game.getDeck().getDeckSize();
	}
}

