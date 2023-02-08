package rs.chat.domain.repository;

import com.google.gson.JsonObject;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import rs.chat.domain.entity.User;
import rs.chat.utils.Constants;

import static java.util.Collections.emptySet;
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
				passwordCode,
				new JsonObject(),
				emptySet(),
				emptySet(),
				emptySet(),
				emptySet(),
				emptySet(),
				emptySet(),
				emptySet()
		);
	}

	@AfterEach
	void tearDown() {
		this.underTest.deleteAll();
	}

	@Test
	@Disabled
	void itShouldFindByUsername() {
		// Given
		this.underTest.save(this.user);

		// When
		User expected = this.underTest.findByUsername(this.username).get();

		// Then
		assertThat(expected).isNotNull();
	}

	@Test
	@Disabled
	void itShouldFindByEmail() {
		// Given
		this.underTest.save(this.user);

		// When
		User expected = this.underTest.findByEmail(this.email).get();

		// Then
		assertThat(expected).isNotNull();
	}

	@Test
	@Disabled
	void itShouldFindByPasswordCode() {
		// Given
		this.underTest.save(this.user);

		// When
		User expected = this.underTest.findByPasswordCode(this.passwordCode).get();

		// Then
		assertThat(expected).isNotNull();
	}
}
