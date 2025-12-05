package domain.game;

import java.util.List;
import java.util.Random;

public class Player {
	private int playerID;
	private List<Card> hand;
	private boolean isDead;
	private Random rand;
	private boolean isCursed;

	private static final String MUST_ADD_DEFUSE_CARD_EXCEPTION = "Must Add Defuse Card";
	private static final String INVALID_INDEX_EXCEPTION = "Invalid Index";
	private static final String NO_CARD_FOUND_EXCEPTION = "No Card Found";

	public Player(int playerID, Instantiator instantiator) {
		this.playerID = playerID;
		this.hand = instantiator.createCardList();
		this.isDead = false;
		this.rand = instantiator.createRandom();
		this.isCursed = false;
	}

	public int getPlayerID() {
		return playerID;
	}

	public void addDefuse(Card defuse) {
		if (defuse.getCardType() != CardType.DEFUSE) {
			throw new IllegalArgumentException(MUST_ADD_DEFUSE_CARD_EXCEPTION);
		}
		hand.add(defuse);
	}

	public void addCardToHand(Card card) {
		hand.add(card);
	}

	public boolean hasCard(CardType cardType) {
		for (Card card : hand) {
			if (card.getCardType() == cardType) {
				return true;
			}
		}
		return false;
	}

	public int getHandSize() {
		return hand.size();
	}

	public Card getCardAt(int index) {
		if (index < 0 || index >= getHandSize()) {
			throw new IllegalArgumentException(INVALID_INDEX_EXCEPTION);
		}
		return hand.get(index);
	}

	public int getIndexOfCard(CardType cardType) {
		for (int i = 0; i < getHandSize(); i++) {
			if (hand.get(i).getCardType() == cardType) {
				return i;
			}
		}
		throw new IllegalArgumentException(NO_CARD_FOUND_EXCEPTION);
	}

	public CardType removeCardFromHand(int index) {
		if (index < 0 || index >= getHandSize()) {
			throw new IllegalArgumentException(NO_CARD_FOUND_EXCEPTION);
		}
		CardType cardType = hand.get(index).getCardType();
		hand.remove(index);
		return cardType;
	}

	public void shuffleHand() {
		for (int swapIndex = getHandSize() - 1; swapIndex > 0; swapIndex--) {
			int indexToSwap = rand.nextInt(swapIndex + 1);
			Card temporaryCard = hand.get(indexToSwap);
			hand.set(indexToSwap, hand.get(swapIndex));
			hand.set(swapIndex, temporaryCard);
		}
	}

	public int checkNumberOfCardsInHand(CardType cardType) {
		int catCounter = 0;
		for (Card card : hand) {
			if (card.getCardType() == cardType) {
				catCounter++;
			}
		}
		return catCounter;
	}

	public boolean getIsDead() {
		return isDead;
	}

	public void setIsDead() {
		isDead = true;
	}

	public boolean getIsCursed() {
		return isCursed;
	}

	public void setCursed(boolean isCursed) {
		this.isCursed = isCursed;
	}

}