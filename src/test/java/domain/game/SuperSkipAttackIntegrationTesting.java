package domain.game;

import io.cucumber.java.en.When;

public class SuperSkipAttackIntegrationTesting {
	private SkipAttackIntegrationTesting skipTest;

	public SuperSkipAttackIntegrationTesting(
			SkipAttackIntegrationTesting test
	) {
		this.skipTest = test;
	}

	@When("the correct player who has been attacked plays one super skip")
	public void the_correct_player_who_has_been_attacked_plays_one_super_skip() {
		skipTest.game.playSkip(true);
	}
}
