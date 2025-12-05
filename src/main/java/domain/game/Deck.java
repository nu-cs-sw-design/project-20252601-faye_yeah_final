package domain.game;

import java.util.List;
import java.util.Random;

public class Deck {
	private List<Card> deck;
	private Random rand;
	private GameType gameType;
	private int numberOfPlayers;
	private int maxDeckSize;
	private Instantiator instantiator;

	private static final String DECK_FULL_EXCEPTION = "Deck is full, cannot insert more cards.";
	private static final String DRAW_FROM_EMPTY_DECK_EXCEPTION =
		"Cannot draw card from empty deck.";
	private static final String NEGATIVE_INDEX_EXCEPTION =
		"Invalid index. Cannot insert into negative index.";
	private static final String INDEX_GREATER_THAN_DECK_SIZE_EXCEPTION =
		"Invalid index. Cannot insert into index greater than deck size.";
	private static final String MISMATCH_ORDER_SIZE_EXCEPTION =
		"Mismatch: The number of indices (%d) does not match the number of cards (%d).";
	private static final String INDEX_OUT_OF_RANGE_EXCEPTION =
		"Index out of range: %d. Valid range is 0 to %d.";


	public Deck(List<Card> deck, Random rand,
				domain.game.GameType gameType, int numberOfPlayers, int maxDeckSize,
				Instantiator instantiator) {
		this.deck = deck;
		this.rand = rand;
		this.gameType = gameType;
		this.numberOfPlayers = numberOfPlayers;
		this.maxDeckSize = maxDeckSize;
		this.instantiator = instantiator;
	}

	public int getDeckSize() {
		return deck.size();
	}

	public Card getCardAtIndex(int index) {
		return deck.get(index);
	}

	public void initializeDeck() {
		final int cardAddedThreeTimes = 3;
		final int cardAddedFourTimes = 4;
		final int cardAddedFiveTimes = 5;
		final int cardAddedOnce = 1;
		final int cardAddedTwice = 2;
		final int streakingKittensExtraCards = 14;
		final int implodingKittensExtraCards = 19;
		insertCard(CardType.NOPE, cardAddedFourTimes, false);
		insertCard(CardType.ATTACK, cardAddedThreeTimes, false);
		insertCard(CardType.SHUFFLE, cardAddedFourTimes, false);
		insertCard(CardType.SKIP, cardAddedThreeTimes, false);
		insertCard(CardType.SEE_THE_FUTURE, cardAddedFourTimes, false);
		insertCard(CardType.CAT_ONE, cardAddedFourTimes, false);
		insertCard(CardType.CAT_TWO, cardAddedFourTimes, false);
		insertCard(CardType.CAT_THREE, cardAddedFourTimes, false);
		insertCard(CardType.CAT_FOUR, cardAddedFourTimes, false);
		insertCard(CardType.DEFUSE,
				cardAddedFiveTimes - numberOfPlayers, false);
		switch (gameType) {
			case STREAKING_KITTENS:
				maxDeckSize = maxDeckSize + streakingKittensExtraCards;
				insertCard(CardType.STREAKING_KITTEN, cardAddedOnce, false);
				insertCard(CardType.ALTER_THE_FUTURE, cardAddedOnce, false);
				insertCard(CardType.CATOMIC_BOMB, cardAddedOnce, false);
				insertCard(CardType.SUPER_SKIP, cardAddedOnce, false);
				insertCard(CardType.SWAP_TOP_AND_BOTTOM,
						cardAddedThreeTimes, false);
				insertCard(CardType.MARK, cardAddedThreeTimes, false);
				insertCard(CardType.SEE_THE_FUTURE, cardAddedOnce, false);
				insertCard(CardType.CURSE_OF_THE_CAT_BUTT, cardAddedTwice, false);
				insertCard(CardType.GARBAGE_COLLECTION, cardAddedOnce, false);
				break;
			case IMPLODING_KITTENS:
				maxDeckSize = maxDeckSize + implodingKittensExtraCards;
				insertCard(CardType.DRAW_FROM_THE_BOTTOM,
						cardAddedFourTimes, false);
				insertCard(CardType.ALTER_THE_FUTURE, cardAddedFourTimes, false);
				insertCard(CardType.FERAL_CAT, cardAddedFourTimes, false);
				insertCard(CardType.REVERSE, cardAddedFourTimes, false);
				insertCard(CardType.TARGETED_ATTACK, cardAddedThreeTimes, false);
				break;
			default:
				break;
		}
	}

	public void shuffleDeck() {
		//Fischer Yates Algorithm
		for (int deckIndex = deck.size() - 1; deckIndex > 0; deckIndex--) {
			int indexToSwap = rand.nextInt(deckIndex + 1);
			Card temporaryCard = deck.get(indexToSwap);
			deck.set(indexToSwap, deck.get(deckIndex));
			deck.set(deckIndex, temporaryCard);
		}
	}

	public void insertCard(CardType cardType, int numberOfCards, boolean bottom) {
		if (addedOutOfBounds(numberOfCards)) {
			throw new UnsupportedOperationException
			(DECK_FULL_EXCEPTION);
		}
		if (!bottom) {
			for (int i = 0; i < numberOfCards; i++) {
				deck.add(deck.size(), instantiator.createCard(cardType));
			}
		} else {
			for (int i = 0; i < numberOfCards; i++) {
				deck.add(0, instantiator.createCard(cardType));
			}
		}
	}

	public Card drawCard() {
		if (this.deck.isEmpty()) {
			throw new UnsupportedOperationException
			(DRAW_FROM_EMPTY_DECK_EXCEPTION);
		}
		else {
			return this.deck.remove(this.deck.size() - 1);
		}
	}

	public Card drawCardFromBottom() {
		if (this.deck.isEmpty()) {
			throw new UnsupportedOperationException
					(DRAW_FROM_EMPTY_DECK_EXCEPTION);
		}
		else {
			return this.deck.remove(0);
		}
	}

	public void setNumberOfPlayers(int numberOfPlayers) {
		this.numberOfPlayers = numberOfPlayers;
	}

	public void chooseGameType(domain.game.GameType gameType) {
		this.gameType = gameType;
	}

	public int removeBombs() {
		int counter = 0;
		for (int index = 0; index < getDeckSize(); index++) {
			if (checkIfExplodingKitten(index)) {
				counter++;
				deck.remove(index);
				index--;
			}
		}
		return counter;
	}

	public void insertExplodingKittenAtIndex(int indexToInsert) {
		if (indexToInsert < 0) {
			throw new UnsupportedOperationException
					(NEGATIVE_INDEX_EXCEPTION);
		} else if (indexToInsert > deck.size()) {
			throw new UnsupportedOperationException
					(INDEX_GREATER_THAN_DECK_SIZE_EXCEPTION);
		} else {
			deck.add(indexToInsert, instantiator.createCard(CardType.EXPLODING_KITTEN));
		}
	}

	public void insertImplodingKittenAtIndex(int indexToInsert, Card card) {
		deck.add(indexToInsert, card);
	}


	protected CardType getCardTypeAtIndex(int index) {
		return deck.get(index).getCardType();
	}

	public void reorderDeckCards(int[] newOrderIndices, List<Card> cardsToReorder) {
		if (newOrderIndices.length != cardsToReorder.size()) {
			final String
				MISMATCH_ORDER_SIZE_EXCEPTION_MESSAGE =
					String.format(MISMATCH_ORDER_SIZE_EXCEPTION,
							newOrderIndices.length,
							cardsToReorder.size());
			throw new IllegalArgumentException(MISMATCH_ORDER_SIZE_EXCEPTION_MESSAGE);
		}

		for (int index : newOrderIndices) {
			if (checkIfIndexOutOfRange(index)) {
				final String
					INDEX_OUT_OF_RANGE_EXCEPTION_MESSAGE =
						String.format(INDEX_OUT_OF_RANGE_EXCEPTION,
								index, deck.size() - 1);
				throw new IndexOutOfBoundsException
						(INDEX_OUT_OF_RANGE_EXCEPTION_MESSAGE);
			}
		}

		for (int i = 0; i < newOrderIndices.length; i++) {
			deck.set(newOrderIndices[i], cardsToReorder.get(i));
		}
	}

	private boolean addedOutOfBounds(int numberOfCards) {
		return (deck.size() + numberOfCards) > maxDeckSize;
	}

	private boolean checkIfExplodingKitten(int index) {
		return deck.get(index).getCardType() == CardType.EXPLODING_KITTEN;
	}

	private boolean checkIfIndexOutOfRange(int index) {
		return index < 0 || index >= deck.size();
	}

}

