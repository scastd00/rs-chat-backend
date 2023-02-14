package rs.chat.domain.repository;

import com.google.gson.JsonObject;
import org.assertj.core.api.AbstractListAssert;
import org.assertj.core.api.ObjectAssert;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import rs.chat.domain.entity.User;
import rs.chat.utils.Constants;

import java.util.List;
import java.util.Optional;

import static java.util.Collections.emptySet;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static rs.chat.Constants.TEST_COMPARISON_CONFIG;

@DataJpaTest
class UserRepositoryTest {

	@Autowired
	private UserRepository underTest;
	private User user;
	private final String username = "david";
	private final String email = "david@hello.com";
	private final String passwordCode = "FNvb23";

	@BeforeEach
	void setUp() {
		this.user = new User(
				1L, username, "12345", email,
				"David Gar Dom", (byte) 21, null, Constants.STUDENT_ROLE,
				null, passwordCode, new JsonObject(), emptySet(),
				emptySet(), emptySet(), emptySet(), emptySet(),
				emptySet(), emptySet()
		);
	}

	@AfterEach
	void tearDown() {
		this.underTest.deleteAll();
	}

	@Test
	void itShouldFindByUsername() {
		// Given
		this.underTest.save(this.user);

		// When
		Optional<User> expected = this.underTest.findByUsername(this.username);

		// Then
		assertThat(expected).isPresent();
	}

	@Test
	void itShouldNotFindByUsername() {
		// Given
		// When
		Optional<User> expected = this.underTest.findByUsername(this.username);

		// Then
		assertThat(expected).isEmpty();
	}

	@Test
	void itShouldFindByEmail() {
		// Given
		this.underTest.save(this.user);

		// When
		Optional<User> expected = this.underTest.findByEmail(this.email);

		// Then
		assertThat(expected).isPresent();
	}

	@Test
	void itShouldNofFindByEmail() {
		// Given
		// When
		Optional<User> expected = this.underTest.findByEmail(this.email);

		// Then
		assertThat(expected).isEmpty();
	}

	@Test
	void itShouldFindByPasswordCode() {
		// Given
		this.underTest.save(this.user);

		// When
		Optional<User> expected = this.underTest.findByPasswordCode(this.passwordCode);

		// Then
		assertThat(expected).isPresent();
	}

	@Test
	void itShouldNotFindByPasswordCode() {
		// Given
		// When
		Optional<User> expected = this.underTest.findByPasswordCode(this.passwordCode);

		// Then
		assertThat(expected).isEmpty();
	}

	@Test
	void itShouldFindAllByRole() {
		// Given
		User user1 = new User(
				null, "jose", "12345", "jose@hello.com",
				"José Dom", (byte) 21, null, Constants.STUDENT_ROLE,
				null, null, new JsonObject(), emptySet(),
				emptySet(), emptySet(), emptySet(), emptySet(),
				emptySet(), emptySet()
		);

		this.underTest.save(this.user);
		this.underTest.save(user1);

		// When
		List<User> expected = this.underTest.findAllByRole(Constants.STUDENT_ROLE);

		// Then
		AbstractListAssert<?, List<?>, Object, ObjectAssert<Object>> listAssert =
				assertThat(expected)
						.asList()
						.hasSize(2);
		listAssert
				.first()
				.usingRecursiveComparison(TEST_COMPARISON_CONFIG)
				.isEqualTo(this.user);
		listAssert
				.last()
				.usingRecursiveComparison(TEST_COMPARISON_CONFIG)
				.isEqualTo(user1);
	}

	@Test
	void itShouldNotFindAllByRole() {
		// Given
		// When
		List<User> expected = this.underTest.findAllByRole(Constants.STUDENT_ROLE);

		// Then
		assertThat(expected).asList().isEmpty();
	}

	@Test
	void itShouldExistByEmail() {
		// Given
		this.underTest.save(this.user);

		// When
		boolean expected = this.underTest.existsByEmail(this.email);

		// Then
		assertThat(expected).isTrue();
	}

	@Test
	void itShouldNotExistByEmail() {
		// Given
		// When
		boolean expected = this.underTest.existsByEmail(this.email);

		// Then
		assertThat(expected).isFalse();
	}
}
