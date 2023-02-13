package rs.chat.domain.repository;

import com.google.gson.JsonObject;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import rs.chat.domain.entity.Session;
import rs.chat.domain.entity.User;
import rs.chat.utils.Constants;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static java.util.Collections.emptySet;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static rs.chat.Constants.TEST_COMPARISON_CONFIG;

@DataJpaTest
class SessionRepositoryTest {

	@Autowired private SessionRepository underTest;
	@Autowired private UserRepository userRepository;
	private Session session;
	private User user;

	@BeforeEach
	void setUp() {
		this.user = this.userRepository.save(new User(
				1L, "david", "12345", "david@hello.com",
				"David Gar Dom", (byte) 21, null, Constants.STUDENT_ROLE,
				null, null, new JsonObject(), emptySet(),
				emptySet(), emptySet(), emptySet(), emptySet(),
				emptySet(), emptySet()
		));

		this.session = new Session(
				1L, "127.0.0.1", Instant.now(), Instant.now().plusSeconds(20), "token", this.user
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
		this.underTest.save(this.session);

		// When
		Optional<Session> expected = this.underTest.findByToken(this.session.getToken());

		// Then
		assertThat(expected)
				.isPresent()
				.get()
				.usingRecursiveComparison(TEST_COMPARISON_CONFIG)
				.isEqualTo(this.session);
	}

	@Test
	void itShouldNotFindByToken() {
		// Given
		this.underTest.save(this.session);

		// When
		Optional<Session> expected = this.underTest.findByToken("invalid");

		// Then
		assertThat(expected).isNotPresent();
	}

	@Test
	void itShouldFindByUserId() {
		// Given
		this.underTest.save(this.session);

		// When
		Optional<Session> expected = this.underTest.findByUserId(this.user.getId());

		// Then
		assertThat(expected)
				.isPresent()
				.get()
				.usingRecursiveComparison(TEST_COMPARISON_CONFIG)
				.isEqualTo(this.session);
	}

	@Test
	void itShouldNotFindByUserId() {
		// Given
		this.underTest.save(this.session);

		// When
		Optional<Session> expected = this.underTest.findByUserId(2L);

		// Then
		assertThat(expected).isNotPresent();
	}

	@Test
	void itShouldFindAllByUserId() {
		// Given
		this.underTest.save(this.session);

		// When
		List<Session> expected = this.underTest.findAllByUserId(this.user.getId());

		// Then
		assertThat(expected)
				.asList()
				.singleElement()
				.usingRecursiveComparison(TEST_COMPARISON_CONFIG)
				.isEqualTo(this.session);
	}

	@Test
	void itShouldNotFindAllByUserId() {
		// Given
		this.underTest.save(this.session);

		// When
		List<Session> expected = this.underTest.findAllByUserId(2L);

		// Then
		assertThat(expected).asList().isEmpty();
	}
}
