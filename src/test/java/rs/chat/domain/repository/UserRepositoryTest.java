package rs.chat.domain.repository;

import com.google.gson.JsonObject;
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

@DataJpaTest
class UserRepositoryTest {

	@Autowired
	private UserRepository underTest;
	private User user1;
	private String username;
	private String email;
	private String passwordCode;

	@BeforeEach
	void setUp() {
		this.username = "david";
		this.email = "david@hello.com";
		this.passwordCode = "FNvb23";

		this.user1 = new User(
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
		this.underTest.save(this.user1);

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
		this.underTest.save(this.user1);

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
		this.underTest.save(this.user1);

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
		this.underTest.save(this.user1);
		User user2 = new User(
				null, "jose", "12345", "jose@hello.com",
				"José Dom", (byte) 21, null, Constants.STUDENT_ROLE,
				null, null, new JsonObject(), emptySet(),
				emptySet(), emptySet(), emptySet(), emptySet(),
				emptySet(), emptySet()
		);
		this.underTest.save(user2);

		// When
		List<User> expected = this.underTest.findAllByRole(Constants.STUDENT_ROLE);

		// Then
		assertThat(expected.toArray(new User[0]))
				.hasSize(2)
				.allMatch(user -> user.getRole().equals(Constants.STUDENT_ROLE));
	}

	@Test
	void itShouldNotFindAllByRole() {
		// Given
		// When
		List<User> expected = this.underTest.findAllByRole(Constants.STUDENT_ROLE);

		// Then
		assertThat(expected.toArray()).isEmpty();
	}

	@Test
	void itShouldExistByEmail() {
		// Given
		this.underTest.save(this.user1);

		// When
		boolean expected = this.underTest.existsByEmail(this.email);

		// Then
		assertThat(expected).isTrue();
	}

	@Test
	void itShouldNotExistByEmail() {
		// Given
		this.underTest.save(this.user1);

		// When
		boolean expected = this.underTest.existsByEmail("email");

		// Then
		assertThat(expected).isFalse();
	}
}
