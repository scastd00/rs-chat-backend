package rs.chat.unit.domain.repository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import rs.chat.domain.entity.Badge;
import rs.chat.domain.repository.BadgeRepository;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static rs.chat.Constants.TEST_COMPARISON_CONFIG;
import static rs.chat.net.ws.Message.TEXT_MESSAGE;

@DataJpaTest
class BadgeRepositoryTest {

	@Autowired
	private BadgeRepository underTest;
	private Badge badge;

	@BeforeEach
	void setUp() {
		this.badge = new Badge(
				null, "1st message", "First message",
				"/images/badges/1st-message.png", TEXT_MESSAGE.type(), 1,
				Collections.emptySet()
		);
	}

	@AfterEach
	void tearDown() {
		this.underTest.deleteAll();
	}

	@Test
	void itShouldFindAllByTypeOrderByPointsOfTypeDesc() {
		// given
		this.underTest.save(this.badge);

		// when
		List<Badge> expected = this.underTest.findAllByTypeOrderByPointsOfTypeDesc(TEXT_MESSAGE.type());

		// then
		assertThat(expected)
				.asList()
				.singleElement()
				.usingRecursiveComparison(TEST_COMPARISON_CONFIG)
				.isEqualTo(this.badge);
	}

	@Test
	void itShouldNotFindAllByTypeOrderByPointsOfTypeDesc() {
		// given
		// when
		List<Badge> expected = this.underTest.findAllByTypeOrderByPointsOfTypeDesc(this.badge.getType());

		// then
		assertThat(expected).asList().isEmpty();
	}
}
