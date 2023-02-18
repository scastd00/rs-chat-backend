package rs.chat.unit.domain.repository;

import org.assertj.core.api.AbstractListAssert;
import org.assertj.core.api.ObjectAssert;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import rs.chat.domain.entity.Session;
import rs.chat.domain.entity.User;
import rs.chat.domain.repository.SessionRepository;
import rs.chat.domain.repository.UserRepository;
import rs.chat.utils.Constants;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static rs.chat.utils.TestConstants.TEST_COMPARISON_CONFIG;
import static rs.chat.utils.TestUtils.createUserWithRole;

@DataJpaTest
class SessionRepositoryTest {

	@Autowired private SessionRepository underTest;
	@Autowired private UserRepository userRepository;
	private Session session1;
	private Session session2;
	private User user;

	@BeforeEach
	void setUp() {
		this.user = this.userRepository.save(createUserWithRole(Constants.STUDENT_ROLE));

		this.session1 = new Session(
				null, "127.0.0.1", Instant.now(), Instant.now().plusSeconds(20), "token1", this.user
		);
		this.session2 = new Session(
				null, "127.0.0.1", Instant.now(), Instant.now().plusSeconds(20), "token2", this.user
		);
	}

	@AfterEach
	void tearDown() {
		this.underTest.deleteAll();
		this.userRepository.deleteAll();
	}

	@Test
	void itShouldFindByToken() {
		// Given
		this.underTest.save(this.session1);

		// When
		Optional<Session> expected = this.underTest.findByToken(this.session1.getToken());

		// Then
		assertThat(expected)
				.isPresent()
				.get()
				.usingRecursiveComparison(TEST_COMPARISON_CONFIG)
				.isEqualTo(this.session1);
	}

	@Test
	void itShouldNotFindByToken() {
		// Given
		// When
		Optional<Session> expected = this.underTest.findByToken(this.session1.getToken());

		// Then
		assertThat(expected).isNotPresent();
	}

	@Test
	void itShouldFindByUserId() {
		// Given
		this.underTest.save(this.session1);

		// When
		Optional<Session> expected = this.underTest.findByUserId(this.user.getId());

		// Then
		assertThat(expected)
				.isPresent()
				.get()
				.usingRecursiveComparison(TEST_COMPARISON_CONFIG)
				.isEqualTo(this.session1);
	}

	@Test
	void itShouldNotFindByUserId() {
		// Given
		// When
		Optional<Session> expected = this.underTest.findByUserId(this.user.getId());

		// Then
		assertThat(expected).isNotPresent();
	}

	@Test
	void itShouldFindAllByUserId() {
		// Given
		this.underTest.save(this.session1);

		// When
		List<Session> expected = this.underTest.findAllByUserId(this.user.getId());

		// Then
		assertThat(expected)
				.asList()
				.singleElement()
				.usingRecursiveComparison(TEST_COMPARISON_CONFIG)
				.isEqualTo(this.session1);
	}

	@Test
	void itShouldFindAllByUserIdMulti() {
		// Given
		this.underTest.save(this.session1);
		this.underTest.save(this.session2);

		// When
		List<Session> expected = this.underTest.findAllByUserId(this.user.getId());

		// Then
		AbstractListAssert<?, List<?>, Object, ObjectAssert<Object>> listAssert =
				assertThat(expected)
						.asList()
						.hasSize(2);
		listAssert
				.first()
				.usingRecursiveComparison(TEST_COMPARISON_CONFIG)
				.isEqualTo(this.session1);
		listAssert
				.last()
				.usingRecursiveComparison(TEST_COMPARISON_CONFIG)
				.isEqualTo(this.session2);
	}

	@Test
	void itShouldNotFindAllByUserId() {
		// Given
		// When
		List<Session> expected = this.underTest.findAllByUserId(this.user.getId());

		// Then
		assertThat(expected).asList().isEmpty();
	}

	@Test
	void itShouldNotFindAllByUserIdMulti() {
		// Given
		this.underTest.save(this.session1);
		this.underTest.save(this.session2);

		// When
		List<Session> expected = this.underTest.findAllByUserId(this.user.getId() + 1);

		// Then
		assertThat(expected).asList().isEmpty();
	}
}
