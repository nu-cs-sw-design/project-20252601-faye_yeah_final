package domain.game;

import java.util.List;
import java.util.Random;

public class Game {
	private int numberOfPlayers;
	private domain.game.GameType gameType;
	private domain.game.Deck deck;
	private domain.game.Player[] players;
	private Random rand;

	private int currentPlayerTurn;
	private int currentPlayerNumberOfTurns;
	private boolean isReversed;
	private List<Integer> attackQueue;
	private int attackCounter;
	private int numberOfAttacks;
	private int[] turnTracker;
	private boolean attacked;

	private static final String PLAYER_HAND_EMPTY_EXCEPTION = "Player has no cards to steal";
	private static final String INVALID_PLAYER_INDEX_EXCEPTION = "Invalid player index.";
	private static final String INVALID_GAME_TYPE_EXCEPTION = "Must Provide a Valid Game Type";
	private static final String NO_PLAYERS_EXCEPTION = "No players to select from.";
	private static final String OUT_OF_BOUNDS_PLAYER_INDEX_EXCEPTION =
			"playerIndex out of Bounds";
	private static final String PLAYER_DEAD_EXCEPTION = "Player is dead";
	private static final String CARD_INDEX_OUT_OF_BOUNDS_EXCEPTION = "cardIndex out of Bounds";
	private static final String CARD_TYPE_NOT_FOUND_EXCEPTION =
			"Player does not have the card type to steal";
	private static final String INVALID_NUMBER_OF_PLAYERS_EXCEPTION =
			"Number of players must be between 2 and 5 inclusive";
	private static final String NUMBER_OF_TURNS_OUT_OF_BOUNDS_EXCEPTION =
			"Number of turns must be between 1 and 6.";

	public Game (int numberOfPlayers, GameType gameType,
                 Deck deck, domain.game.Player[] players, Random rand,
                 List<Integer> attackQueue,
                 int[] turnTracker) {
		this.numberOfPlayers = numberOfPlayers;
		this.gameType = gameType;
		this.deck = deck;
		this.players = players;
		this.rand = rand;
		this.currentPlayerTurn = 0;
		this.currentPlayerNumberOfTurns = 0;
		this.attackQueue = attackQueue;
		isReversed = false;
		this.turnTracker = turnTracker;
		this.attackCounter = 0;
		this.numberOfAttacks = 0;
		this.attacked = false;
	}

	public void swapTopAndBottom() {
		if (checkDeckHasOneCardOrLess()) {
			return;
		}
		Card bottomCard = deck.drawCardFromBottom();
		Card topCard = drawCard();
		deck.insertCard(bottomCard.getCardType(), 1, false);
		deck.insertCard(topCard.getCardType(), 1, true);
	}

	public Card stealRandomCard(int playerToStealFrom) {
		domain.game.Player player = players[playerToStealFrom];
		if (checkPlayerHandEmpty(player)) {
			throw new IllegalArgumentException(PLAYER_HAND_EMPTY_EXCEPTION);
		}

		int randomCardIndex = rand.nextInt(player.getHandSize());
		Card stealedCard = player.getCardAt(randomCardIndex);
		player.removeCardFromHand(randomCardIndex);
		addCardToHand(stealedCard);
		return stealedCard;
	}

	public void stealTypeCard(CardType cardType, int playerToStealFrom) {
		domain.game.Player player = players[playerToStealFrom];
		try {
			int cardIndex = getIndexOfCardFromHand(playerToStealFrom,
					cardType);
			Card stealedCard = player.getCardAt(cardIndex);
			player.removeCardFromHand(cardIndex);
			players[currentPlayerTurn].addCardToHand(stealedCard);
		}
		catch (IllegalArgumentException e) {
			throw new IllegalArgumentException
					(CARD_TYPE_NOT_FOUND_EXCEPTION);
		}
	}


	public void startAttackPhase() {

		final int attackedAttackThreshold = 4;
		attackCounter = currentPlayerTurn;
		numberOfAttacks = 0;
		while (!isAttackQueueEmpty()) {
			int attack = removeAttackQueue();
			if (attack <= attackedAttackThreshold) {
				playTargetedAttack(attack);
			} else {
				playAttack();
			}
		}
		if (turnTracker[attackCounter] == 1) {
			turnTracker[attackCounter] = 0;
		}
		if (attackCounter == currentPlayerTurn) {
			currentPlayerNumberOfTurns += numberOfAttacks;
			turnTracker[attackCounter] = 1;
			attacked = false;
		} else {
			turnTracker[attackCounter] += numberOfAttacks;
		}
		decrementNumberOfTurns();
		if (checkIfNumberOfTurnsIsZero()) {
			incrementPlayerTurn();
		}
	}

	public void playAttack() {
		incrementAttackCounter();
		addAttacks();
		attacked = true;
	}

	public void playTargetedAttack (int attackedPlayerIndex) {
		setAttackCounter(attackedPlayerIndex);
		addAttacks();
		attacked = true;
	}

	public boolean playExplodingKitten(int playerIndex) {
		if (checkUserOutOfBounds(playerIndex)) {
			throw new UnsupportedOperationException(INVALID_PLAYER_INDEX_EXCEPTION);
		}
		if (checkIfPlayerHasCard(playerIndex, CardType.DEFUSE)) {
			return false;
		}
		players[playerIndex].setIsDead();
		if (playerIndex == currentPlayerTurn) {
			setCurrentPlayerNumberOfTurns(0);
		}
		return true;
	}

	public void playImplodingKitten() {
		setCurrentPlayerNumberOfTurns(0);
		players[currentPlayerTurn].setIsDead();
	}

	public void playDefuse(int idxToInsertExplodingKitten, int playerIndex) {
		if (checkUserOutOfBounds(playerIndex)) {
			throw new UnsupportedOperationException(INVALID_PLAYER_INDEX_EXCEPTION);
		}
		Player currentPlayer = players[playerIndex];
		try {
			deck.insertExplodingKittenAtIndex(idxToInsertExplodingKitten);
		} catch (UnsupportedOperationException e) {
			throw e;
		}
		int defuseIdx = currentPlayer.getIndexOfCard(CardType.DEFUSE);
		currentPlayer.removeCardFromHand(defuseIdx);

		if (playerIndex == currentPlayerTurn) {
			setCurrentPlayerNumberOfTurns(0);
		}

	}

	public Card drawFromBottom() {
		return deck.drawCardFromBottom();
	}

	public void playCatomicBomb() {
		int numberOfBombs = deck.removeBombs();
		deck.insertCard(CardType.EXPLODING_KITTEN, numberOfBombs, false);
		decrementNumberOfTurns();
		if (checkIfNumberOfTurnsIsZero()) {
			incrementPlayerTurn();
		}
	}

	public void setNumberOfPlayers (int numberOfPlayers) {
		if (checkInvalidNumberOfPlayers(numberOfPlayers)) {
			throw new IllegalArgumentException
					(INVALID_NUMBER_OF_PLAYERS_EXCEPTION);
		}
		this.numberOfPlayers = numberOfPlayers;
		getDeck().setNumberOfPlayers(numberOfPlayers);
	}

	public void playReverse() {
		int startPointer = 0;
		int endPointer = numberOfPlayers - 1;
		isReversed = !isReversed;
		while (startPointer < endPointer) {
			domain.game.Player temporaryPlayerOne = players[startPointer];
			domain.game.Player temporaryPlayerTwo = players[endPointer];
			players[startPointer] = temporaryPlayerTwo;
			players[endPointer] = temporaryPlayerOne;
			startPointer++;
			endPointer--;
		}
		currentPlayerTurn = numberOfPlayers - currentPlayerTurn - 1;
		decrementNumberOfTurns();
		if (checkIfNumberOfTurnsIsZero()) {
			incrementPlayerTurn();
		}
	}

	public void retrieveGameMode(GameType gameType) {
		if (matchingGameType(gameType)) {
			throw new IllegalArgumentException(INVALID_GAME_TYPE_EXCEPTION);
		}
		this.gameType = gameType;
		deck.chooseGameType(gameType);
	}

	public domain.game.Player selectRandomPlayer() {
		int randomPlayerIndex = rand.nextInt(numberOfPlayers);
		if (hasZeroPlayers()) {
			throw new UnsupportedOperationException(NO_PLAYERS_EXCEPTION);
		}
		return players[randomPlayerIndex];
	}

	public void playShuffle(int numberOfShuffles) {
		for (int i = 0; i < numberOfShuffles; i++) {
			deck.shuffleDeck();
		}
	}


	public int playSkip(boolean superSkip) {
		if (checkIfNumberOfTurnsOutOfBounds()) {
			throw new UnsupportedOperationException(
					NUMBER_OF_TURNS_OUT_OF_BOUNDS_EXCEPTION);
		}
		if (superSkip) {
			setCurrentPlayerNumberOfTurns(0);
		} else {
			currentPlayerNumberOfTurns--;
		}
		if (checkIfNumberOfTurnsIsZero()) {
			incrementPlayerTurn();
		}
		return currentPlayerNumberOfTurns;
	}

	public void playGarbageCollection(CardType cardToDiscard) {
		deck.insertCard(cardToDiscard, 1, false);
		deck.shuffleDeck();
	}

	public Deck getDeck() {
		return deck;
	}

	public void incrementPlayerTurn() {
		do {
			currentPlayerTurn = (currentPlayerTurn + 1) % numberOfPlayers;
		} while (checkIfPlayerDead(currentPlayerTurn));
	}

	public void incrementAttackCounter() {
		do {
			attackCounter = (attackCounter + 1) % numberOfPlayers;
		} while (checkIfPlayerDead(attackCounter));
	}

	public void setAttackCounter(int playerIndex) {
		attackCounter = playerIndex;
	}

	GameType getGameTypeForTesting() {
		return gameType;
	}

	public GameType getGameType() {
		return gameType;
	}

	public domain.game.Player getPlayerAtIndex(int playerIndex) {
		return players[playerIndex];
	}

	public void addAttacks() {
		numberOfAttacks++;
		numberOfAttacks++;
	}

	public void playMark(int playerIndex, int cardIndex) {
		if (checkUserOutOfBounds(playerIndex)) {
			throw new IllegalArgumentException(OUT_OF_BOUNDS_PLAYER_INDEX_EXCEPTION);
		}
		if (checkIfPlayerDead(playerIndex)) {
			throw new IllegalArgumentException(PLAYER_DEAD_EXCEPTION);
		}
		if (checkCardOutOfBoundsIndexed(cardIndex, playerIndex)) {
			throw new IllegalArgumentException(CARD_INDEX_OUT_OF_BOUNDS_EXCEPTION);
		}
		Card card = getPlayerAtIndex(playerIndex).getCardAt(cardIndex);
		card.markCard();

	}

	public void addAttackQueue(int attack) {
		attackQueue.add(attack);
	}

	public int removeAttackQueue() {
		return attackQueue.remove(0);
	}

	public boolean isAttackQueueEmpty() {
		return attackQueue.isEmpty();
	}

	public void setPlayerNumberOfTurns() {
		currentPlayerNumberOfTurns = turnTracker[currentPlayerTurn];
		turnTracker[currentPlayerTurn] = 1;
	}

	public int getPlayerTurn() {
		return currentPlayerTurn;
	}

	public int getNumberOfPlayers() {
		return numberOfPlayers;
	}

	public int checkNumberOfAlivePlayers() {
		int counter = 0;
		for (int playerIndex = 0; playerIndex < numberOfPlayers; playerIndex++) {
			domain.game.Player player = getPlayerAtIndex(playerIndex);
			if (!player.getIsDead()) {
				counter++;
			}
		}
		return counter;
	}

	public void setCurrentPlayerNumberOfTurns(int numberOfTurns) {
		currentPlayerNumberOfTurns = numberOfTurns;
	}

	public void decrementNumberOfTurns() {
		currentPlayerNumberOfTurns--;
	}

	public int getNumberOfTurns() {
		return currentPlayerNumberOfTurns;
	}

	public int getDeckSize() {
		return deck.getDeckSize();
	}

	public Card drawCard() {
		return deck.drawCard();
	}

	public Card getCardAtIndex(int cardIndex) {
		return deck.getCardAtIndex(cardIndex);
	}

	public void removeCardFromHand(int playerIndex, CardType cardType) {
		getPlayerAtIndex(playerIndex).removeCardFromHand(
				getIndexOfCardFromHand(playerIndex, cardType));
	}

	public int getIndexOfCardFromHand(int playerIndex, CardType cardType) {
		return getPlayerAtIndex(playerIndex)
				.getIndexOfCard(cardType);
	}

	public void addCardToHand(Card card) {
		getPlayerAtIndex(currentPlayerTurn).addCardToHand(card);
	}

	public boolean checkIfPlayerDead(int playerIndex) {
		return getPlayerAtIndex(playerIndex).getIsDead();
	}

	public boolean checkIfPlayerHasCard(int playerIndex, CardType cardType) {
		return getPlayerAtIndex(playerIndex).hasCard(cardType);
	}

	public CardType getCardType(int playerIndex, int cardIndex) {
		return getPlayerAtIndex(playerIndex).getCardAt(cardIndex).getCardType();
	}

	public int getHandSize(int playerIndex) {
		return getPlayerAtIndex(playerIndex).getHandSize();
	}

	public CardType getDeckCardType(int deckIndex) {
		return getCardAtIndex(deckIndex).getCardType();
	}

	public boolean getIsReversed() {
		return isReversed;
	}

	protected void setCurrentPlayerTurn(int turn) {
		currentPlayerTurn = turn;

	}

	private boolean matchingGameType (GameType gameType) {
		return gameType == GameType.NONE;
	}

	private boolean checkCardOutOfBoundsIndexed(int cardIndex, int playerIndex) {
		return cardIndex > getHandSize(playerIndex) - 1
				|| cardIndex < 0;
	}

	private boolean checkUserOutOfBounds(int userIndex) {
		return userIndex < 0 || userIndex >= getNumberOfPlayers();
	}

	private boolean checkDeckHasOneCardOrLess() {
		return deck.getDeckSize() <= 1;
	}

	private boolean checkPlayerHandEmpty(domain.game.Player player) {
		return player.getHandSize() == 0;
	}


	private boolean checkInvalidNumberOfPlayers(int numPlayers) {
		final int minPlayerThreshold = 1;
		final int maxPlayerThreshold = 6;
		return numPlayers <= minPlayerThreshold
				|| numPlayers >= maxPlayerThreshold;
	}

	private boolean hasZeroPlayers() {
		return numberOfPlayers == 0;
	}


	private boolean checkIfNumberOfTurnsOutOfBounds() {
		final int minNumberOfTurnsThreshold = 1;
		final int maxNumberOfTurnsThreshold = 6;
		return currentPlayerNumberOfTurns < minNumberOfTurnsThreshold
				|| currentPlayerNumberOfTurns > maxNumberOfTurnsThreshold;
	}

	private boolean checkIfNumberOfTurnsIsZero() {
		return currentPlayerNumberOfTurns == 0;
	}

	public void setTurnToTargetedIndexIfAttackOccurred() {
		if (attacked) {
			attacked = false;
			currentPlayerTurn = attackCounter;
			if (checkIfPlayerDead(currentPlayerTurn)) {
				incrementPlayerTurn();
			}
		}
	}

	public int getTurnCountOfPlayer(int playerIndex) {
		return turnTracker[playerIndex];
	}

	public boolean getAttacked() {
		return attacked;
	}

	public int getAttackCounter() {
		return attackCounter;
	}

	public int getNumberOfAttacks() {
		return numberOfAttacks;
	}

	void setNumberOfAttacks(int numberOfAttacks) {
		this.numberOfAttacks = numberOfAttacks;
	}

	void setAttacked(boolean attacked) {
		this.attacked = attacked;
	}
}