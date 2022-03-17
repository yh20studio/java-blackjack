package blackjack.domain.role;

import static blackjack.domain.card.Denomination.ACE;
import static blackjack.domain.card.Denomination.EIGHT;
import static blackjack.domain.card.Denomination.FIVE;
import static blackjack.domain.card.Denomination.KING;
import static blackjack.domain.card.Denomination.SEVEN;
import static blackjack.domain.card.Denomination.SIX;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import blackjack.domain.card.Card;
import blackjack.factory.CardMockFactory;
import blackjack.factory.HandMockFactory;
import blackjack.util.CreateHand;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

@DisplayName("Dealer 테스트")
class DealerTest {

	@DisplayName("딜러가 현재 가지고 있는 패에 따라 한 장을 더 드로우 할지 말지를 결정한다")
	@ParameterizedTest(name = "{index} {displayName} hand={0} expectedResult={1} drawSelect={2}")
	@MethodSource("createHand")
	void drawableTest(final Hand hand, final boolean expectedResult, final boolean drawSelect) {
		Role dealer = new Dealer(hand, () -> drawSelect);
		assertThat(dealer.canDraw()).isEqualTo(expectedResult);
	}

	private static Stream<Arguments> createHand() {
		final Hand matchOptimalCase = HandMockFactory.getBlackJackHand();
		final Hand randomDrawWithAce = CreateHand.create(CardMockFactory.of(ACE),
				CardMockFactory.of(EIGHT));
		final Hand drawWithAce = CreateHand.create(CardMockFactory.of(ACE),
				CardMockFactory.of(FIVE));
		final Hand notDrawWithoutAce = CreateHand.create(CardMockFactory.of(SEVEN),
				CardMockFactory.of(KING));
		final Hand drawWithoutAce = CreateHand.create(CardMockFactory.of(SIX),
				CardMockFactory.of(KING));

		return Stream.of(
				Arguments.of(matchOptimalCase, false, true),
				Arguments.of(randomDrawWithAce, false, false),
				Arguments.of(randomDrawWithAce, true, true),
				Arguments.of(drawWithAce, true, false),
				Arguments.of(notDrawWithoutAce, false, true),
				Arguments.of(drawWithoutAce, true, false)
		);
	}

	@Test
	@DisplayName("딜러의 패 오픈 전략 확인")
	void check_Open_Hand() {
		final Hand hand = CreateHand.create(CardMockFactory.of(ACE), CardMockFactory.of(KING));
		Role dealer = new Dealer(hand, DealerDrawChoice::chooseDraw);
		List<Card> expectedOpenCards = List.of(CardMockFactory.of(ACE));
		assertThat(dealer.openHand()).isEqualTo(expectedOpenCards);
	}
}

