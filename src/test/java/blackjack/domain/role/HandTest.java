package blackjack.domain.role;

import static blackjack.domain.card.Denomination.ACE;
import static blackjack.domain.card.Denomination.FOUR;
import static blackjack.domain.card.Denomination.JACK;
import static blackjack.domain.card.Denomination.KING;
import static blackjack.domain.card.Denomination.NINE;
import static blackjack.domain.card.Denomination.TEN;
import static blackjack.domain.card.Denomination.THREE;
import static blackjack.domain.card.Denomination.TWO;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import blackjack.domain.card.Card;
import blackjack.domain.card.Denomination;
import blackjack.factory.CardMockFactory;
import blackjack.util.CreateHand;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;

@DisplayName("Hand 테스트")
class HandTest {

	@Test
	@DisplayName("드로우한 카드가 제대로 추가되는지 확인")
	void addCard() {
		final Hand hand = new Hand();
		final Card card = CardMockFactory.of(ACE);

		hand.addCard(card);
		final List<Card> expectedHand = List.of(card);
		final List<Card> actualHand = hand.getCards();

		assertThat(actualHand.containsAll(expectedHand)).isTrue();
	}

	@DisplayName("패에 Ace 카드가 있는지 확인")
	@ParameterizedTest(name = "{index} {displayName} card={0} expectedHasAce={1}")
	@CsvSource(value = {"ACE, true", "KING, false"})
	void check_Has_Ace(Denomination cardInfo, boolean expectedHasAce) {
		final Hand hand = new Hand();
		final Card card = CardMockFactory.of(cardInfo);

		hand.addCard(card);

		assertThat(hand.hasAce()).isEqualTo(expectedHasAce);
	}

	@DisplayName("패가 파산 했는지 확인")
	@ParameterizedTest(name = "{index} {displayName} score={0} expectedIsBustScore={1}")
	@CsvSource(value = {"1, false", "21, false", "22, true"})
	void check_Is_Bust(int score, boolean expectedIsBustScore) {
		final Hand hand = new Hand();
		assertThat(hand.isBust(score)).isEqualTo(expectedIsBustScore);
	}

	@DisplayName("패가 블랙잭인지 확인")
	@ParameterizedTest(name = "{index} {displayName} cards={0} expectedBlackJack={1}")
	@MethodSource("getHandAndBlackJack")
	void check_Is_BlackJack(Hand hand, boolean expectedBlackJack) {
		assertThat(hand.isBlackJack()).isEqualTo(expectedBlackJack);
	}

	private static Stream<Arguments> getHandAndBlackJack() {
		final Hand hand1 = CreateHand.create(CardMockFactory.of(ACE), CardMockFactory.of(KING));
		final Hand hand2 = CreateHand.create(CardMockFactory.of(KING), CardMockFactory.of(JACK));
		final Hand hand3 = CreateHand.create(CardMockFactory.of(ACE), CardMockFactory.of(NINE),
				CardMockFactory.of(TWO));
		final Hand hand4 = CreateHand.create(CardMockFactory.of(TEN), CardMockFactory.of(KING),
				CardMockFactory.of(JACK));
		final Hand hand5 = CreateHand.create(CardMockFactory.of(TWO), CardMockFactory.of(THREE),
				CardMockFactory.of(FOUR));

		return Stream.of(
				Arguments.of(hand1, true),
				Arguments.of(hand2, false),
				Arguments.of(hand3, false),
				Arguments.of(hand4, false),
				Arguments.of(hand5, false));
	}

	@DisplayName("현재 패에 가지고 있는 최적의 점수 계산 확인")
	@ParameterizedTest(name = "{index} {displayName} cards={0}")
	@MethodSource("getHandAndScore")
	void check_Calculated_Score(Hand hand, int expectedScore) {
		final int actualScore = hand.calculateOptimalScore();
		assertThat(actualScore).isEqualTo(expectedScore);
	}

	private static Stream<Arguments> getHandAndScore() {
		final Hand hand1 = CreateHand.create(CardMockFactory.of(ACE), CardMockFactory.of(KING));
		final Hand hand2 = CreateHand.create(CardMockFactory.of(ACE), CardMockFactory.of(KING),
				CardMockFactory.of(JACK));
		final Hand hand3 = CreateHand.create(CardMockFactory.of(TEN), CardMockFactory.of(KING),
				CardMockFactory.of(JACK));
		final Hand hand4 = CreateHand.create(CardMockFactory.of(TEN), CardMockFactory.of(KING));

		return Stream.of(
				Arguments.of(hand1, 21),
				Arguments.of(hand2, 21),
				Arguments.of(hand3, 30),
				Arguments.of(hand4, 20));
	}
}
