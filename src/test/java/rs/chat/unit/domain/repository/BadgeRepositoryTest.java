package rs.chat.unit.domain.repository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import rs.chat.domain.entity.Badge;
import rs.chat.domain.repository.BadgeRepository;
import rs.chat.utils.factories.DefaultFactory;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static rs.chat.net.ws.Message.TEXT_MESSAGE;
import static rs.chat.utils.TestConstants.TEST_COMPARISON_CONFIG;

@DataJpaTest
class BadgeRepositoryTest {

	@Autowired
	private BadgeRepository underTest;
	private Badge badge;

	@BeforeEach
	void setUp() {
		this.badge = DefaultFactory.INSTANCE.createBadge(null, "1st message", "First message", 1);
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
