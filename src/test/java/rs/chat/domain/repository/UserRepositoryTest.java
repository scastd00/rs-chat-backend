package rs.chat.domain.repository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import rs.chat.domain.entity.User;
import rs.chat.utils.Constants;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataJpaTest
class UserRepositoryTest {

	@Autowired
	private UserRepository underTest;

	@AfterEach
	void tearDown() {
		this.underTest.deleteAll();
	}

	@Test
	void itShouldFindByUsername() {
		// Given
		String username = "david";
		User user = new User(
				1L,
				username,
				"12345",
				"david@hello.com",
				"David Gar Dom",
				(byte) 21,
				null,
				Constants.STUDENT_ROLE,
				null,
				null
		);
		this.underTest.save(user);

		// When
		User expected = this.underTest.findByUsername(username);

		// Then
		assertThat(expected).isNotNull();
	}

	@Test
	void itShouldFindByEmail() {
		// Given
		String email = "david@hello.com";
		User user = new User(
				1L,
				"david",
				"12345",
				email,
				"David Gar Dom",
				(byte) 21,
				null,
				Constants.STUDENT_ROLE,
				null,
				null
		);
		this.underTest.save(user);

		// When
		User expected = this.underTest.findByEmail(email);

		// Then
		assertThat(expected).isNotNull();
	}

	@Test
	void itShouldFindByPasswordCode() {
		// Given
		String passwordCode = "FNvb23";
		User user = new User(
				1L,
				"david",
				"12345",
				"david@hello.com",
				"David Gar Dom",
				(byte) 21,
				null,
				Constants.STUDENT_ROLE,
				null,
				passwordCode
		);
		this.underTest.save(user);

		// When
		User expected = this.underTest.findByPasswordCode(passwordCode);

		// Then
		assertThat(expected).isNotNull();
	}
}
