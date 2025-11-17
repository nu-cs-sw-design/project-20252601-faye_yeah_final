package domain.game;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;


import static org.junit.jupiter.api.Assertions.*;

public class CardTest {

	@Test
	public void testCardNotMarked() {

		Card card = new Card(CardType.NOPE);

		assertFalse(card.checkIfMarked());
	}

	@Test
	public void testCardMarked() {

		Card card = new Card(CardType.NOPE);

		assertFalse(card.checkIfMarked());
		card.markCard();
		assertTrue(card.checkIfMarked());
	}

	@ParameterizedTest
	@EnumSource(names = {"NOPE", "DEFUSE", "ATTACK",
			"SHUFFLE", "SKIP", "SEE_THE_FUTURE", "CAT_ONE",
			"CAT_TWO", "CAT_THREE", "CAT_FOUR", "EXPLODING_KITTEN",
			"STREAKING_KITTEN", "SWAP_TOP_AND_BOTTOM", "GARBAGE_COLLECTION",
			"CURSE_OF_THE_CAT_BUTT", "ALTER_THE_FUTURE", "CATOMIC_BOMB",
			"SUPER_SKIP", "MARK", "IMPLODING_KITTEN", "REVERSE",
			"FERAL_CAT", "TARGETED_ATTACK", "DRAW_FROM_THE_BOTTOM"
			})
	public void getCardType(CardType cardType) {
		Card card = new Card(cardType);
		assertEquals(card.getCardType(), cardType);
	}

	@Test
	public void testCardNotFacedUp() {

		Card card = new Card(CardType.SKIP);

		assertFalse(card.checkIfFacedUp());
	}

	@Test
	public void testCardFacedUp() {

		Card card = new Card(CardType.SKIP);

		assertFalse(card.checkIfFacedUp());
		card.setFacedUp();
		assertTrue(card.checkIfFacedUp());
	}

	@Test
	public void setFacedUpTrue() {

		Card card = new Card(CardType.SKIP);

		card.setFacedUp();
		assertTrue(card.checkIfFacedUp());
	}

}
