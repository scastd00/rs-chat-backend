package rs.chat.domain.repository;

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

	@Test
	void itShouldFindByUsername() {
		// Given
		User user = new User(
				1L,
				"david",
				"12345",
				"david@hello.com",
				"David Gar Dom",
				(byte) 21,
				null,
				Constants.STUDENT_ROLE,
				null
		);
		underTest.save(user);

		// When
		String username = "david";
		User expected = underTest.findByUsername(username);

		// Then
		assertThat(expected).isNotNull();
	}
}
