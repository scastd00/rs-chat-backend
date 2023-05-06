package rs.chat.unit.domain.repository;

import org.assertj.core.api.AbstractListAssert;
import org.assertj.core.api.ObjectAssert;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import rs.chat.Constants;
import rs.chat.domain.entity.Badge;
import rs.chat.domain.entity.User;
import rs.chat.domain.entity.UserBadge;
import rs.chat.domain.entity.UserBadgeId;
import rs.chat.domain.repository.BadgeRepository;
import rs.chat.domain.repository.UserBadgeRepository;
import rs.chat.domain.repository.UserRepository;
import rs.chat.utils.factories.DefaultFactory;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static rs.chat.utils.TestConstants.TEST_COMPARISON_CONFIG;

@DataJpaTest
class UserBadgeRepositoryTest {
	@Autowired private UserBadgeRepository underTest;
	@Autowired private UserRepository userRepository;
	@Autowired private BadgeRepository badgeRepository;
	private User user;
	private Badge badge1;
	private Badge badge2;
	private UserBadge userBadge1;
	private UserBadge userBadge2;
	private UserBadgeId userBadgeId1;
	private UserBadgeId userBadgeId2;

	@BeforeEach
	void setUp() {
		this.user = this.userRepository.save(DefaultFactory.INSTANCE.createUser(null, Constants.STUDENT_ROLE));
		this.badge1 = this.badgeRepository.save(DefaultFactory.INSTANCE.createBadge(null, "1st message", "First message", 1));
		this.badge2 = this.badgeRepository.save(DefaultFactory.INSTANCE.createBadge(null, "2nd message", "Second message", 2));
		this.userBadgeId1 = new UserBadgeId(this.user.getId(), this.badge1.getId());
		this.userBadgeId2 = new UserBadgeId(this.user.getId(), this.badge2.getId());
		this.userBadge1 = new UserBadge(this.userBadgeId1, this.user, this.badge1);
		this.userBadge2 = new UserBadge(this.userBadgeId2, this.user, this.badge2);
	}

	@AfterEach
	void tearDown() {
		this.underTest.deleteAll();
		this.userRepository.deleteAll();
		this.badgeRepository.deleteAll();
	}

	@Test
	void itShouldExistsById_UserIdAndId_BadgeId() {
		// Given
		this.underTest.save(this.userBadge1);
		this.underTest.save(this.userBadge2);

		// When
		boolean expected1 = this.underTest.existsById_UserIdAndId_BadgeId(
				this.userBadgeId1.getUserId(), this.userBadgeId1.getBadgeId()
		);
		boolean expected2 = this.underTest.existsById_UserIdAndId_BadgeId(
				this.userBadgeId1.getUserId(), this.userBadgeId1.getBadgeId()
		);

		// Then
		assertThat(expected1).isTrue();
		assertThat(expected2).isTrue();
	}

	@Test
	void itShouldNotExistsById_UserIdAndId_BadgeId() {
		// Given
		// When
		boolean expected1 = this.underTest.existsById_UserIdAndId_BadgeId(
				this.userBadgeId1.getUserId(), this.userBadgeId1.getBadgeId()
		);
		boolean expected2 = this.underTest.existsById_UserIdAndId_BadgeId(
				this.userBadgeId1.getUserId(), this.userBadgeId1.getBadgeId()
		);

		// Then
		assertThat(expected1).isFalse();
		assertThat(expected2).isFalse();
	}

	@Test
	void itShouldFindAllById_UserId() {
		// Given
		this.underTest.save(this.userBadge1);
		this.underTest.save(this.userBadge2);

		// When
		List<UserBadge> expected = this.underTest.findAllById_UserId(this.user.getId());

		// Then
		AbstractListAssert<?, List<?>, Object, ObjectAssert<Object>> listAssert =
				assertThat(expected)
						.asList()
						.hasSize(2);
		listAssert
				.first()
				.usingRecursiveComparison(TEST_COMPARISON_CONFIG)
				.isEqualTo(this.userBadge1);
		listAssert
				.last()
				.usingRecursiveComparison(TEST_COMPARISON_CONFIG)
				.isEqualTo(this.userBadge2);
	}

	@Test
	void itShouldNotFindAllById_UserIdNoElements() {
		// Given
		// When
		List<UserBadge> expected = this.underTest.findAllById_UserId(this.user.getId());

		// Then
		assertThat(expected).asList().isEmpty();
	}

	@Test
	void itShouldNotFindAllById_UserIdIncorrectUser() {
		// Given
		this.underTest.save(this.userBadge1);
		this.underTest.save(this.userBadge2);

		// When
		List<UserBadge> expected = this.underTest.findAllById_UserId(this.user.getId() + 1);

		// Then
		assertThat(expected).asList().isEmpty();
	}
}
