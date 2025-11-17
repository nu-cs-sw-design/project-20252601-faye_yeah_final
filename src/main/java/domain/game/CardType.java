package domain.game;

public enum CardType {
	NOPE(domain.game.GameType.EXPLODING_KITTENS),
	DEFUSE(domain.game.GameType.EXPLODING_KITTENS),
	ATTACK(domain.game.GameType.EXPLODING_KITTENS),
	SHUFFLE(domain.game.GameType.EXPLODING_KITTENS),
	SKIP(domain.game.GameType.EXPLODING_KITTENS),
	SEE_THE_FUTURE(domain.game.GameType.EXPLODING_KITTENS),
	CAT_ONE(domain.game.GameType.EXPLODING_KITTENS),
	CAT_TWO(domain.game.GameType.EXPLODING_KITTENS),
	CAT_THREE(domain.game.GameType.EXPLODING_KITTENS),
	CAT_FOUR(domain.game.GameType.EXPLODING_KITTENS),
	EXPLODING_KITTEN(domain.game.GameType.EXPLODING_KITTENS),
	STREAKING_KITTEN(domain.game.GameType.STREAKING_KITTENS),
	SWAP_TOP_AND_BOTTOM(domain.game.GameType.STREAKING_KITTENS),
	GARBAGE_COLLECTION(domain.game.GameType.STREAKING_KITTENS),
	CURSE_OF_THE_CAT_BUTT(domain.game.GameType.STREAKING_KITTENS),
	ALTER_THE_FUTURE(domain.game.GameType.STREAKING_KITTENS),
	CATOMIC_BOMB(domain.game.GameType.STREAKING_KITTENS),
	SUPER_SKIP(GameType.STREAKING_KITTENS),
	MARK(domain.game.GameType.STREAKING_KITTENS),
	IMPLODING_KITTEN(domain.game.GameType.IMPLODING_KITTENS),
	REVERSE(domain.game.GameType.IMPLODING_KITTENS),
	FERAL_CAT(domain.game.GameType.IMPLODING_KITTENS),
	TARGETED_ATTACK(domain.game.GameType.IMPLODING_KITTENS),
	DRAW_FROM_THE_BOTTOM(domain.game.GameType.IMPLODING_KITTENS);

	private final domain.game.GameType gameType;

	CardType(domain.game.GameType gameType) {
		this.gameType = gameType;
	}
}
