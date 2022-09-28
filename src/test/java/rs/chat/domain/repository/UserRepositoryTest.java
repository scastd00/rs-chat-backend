package rs.chat.domain.repository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
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
	private User user;
	private String username;
	private String email;
	private String passwordCode;

	@BeforeEach
	void setUp() {
		this.username = "david";
		this.email = "david@hello.com";
		this.passwordCode = "FNvb23";

		this.user = new User(
				1L,
				username,
				"12345",
				email,
				"David Gar Dom",
				(byte) 21,
				null,
				Constants.STUDENT_ROLE,
				null,
				passwordCode
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
		User expected = this.underTest.findByUsername(this.username);

		// Then
		assertThat(expected).isNotNull();
	}

	@Test
	void itShouldFindByEmail() {
		// Given
		this.underTest.save(this.user);

		// When
		User expected = this.underTest.findByEmail(this.email);

		// Then
		assertThat(expected).isNotNull();
	}

	@Test
	void itShouldFindByPasswordCode() {
		// Given
		this.underTest.save(this.user);

		// When
		User expected = this.underTest.findByPasswordCode(this.passwordCode);

		// Then
		assertThat(expected).isNotNull();
	}
}
