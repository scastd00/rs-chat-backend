package rs.chat.unit.domain.repository;

import com.google.gson.JsonObject;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import rs.chat.domain.entity.File;
import rs.chat.domain.entity.User;
import rs.chat.domain.repository.FileRepository;
import rs.chat.domain.repository.UserRepository;
import rs.chat.utils.Constants;

import java.time.Instant;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static rs.chat.utils.TestConstants.TEST_COMPARISON_CONFIG;
import static rs.chat.utils.TestUtils.createUserWithRole;

@DataJpaTest
class FileRepositoryTest {

	@Autowired private FileRepository underTest;
	@Autowired private UserRepository userRepository;
	private File file;
	private User user;

	@BeforeEach
	void setUp() {
		this.user = this.userRepository.save(createUserWithRole(Constants.STUDENT_ROLE));

		this.file = new File(
				null, "name", Instant.now(), 1024,
				"/path/to/file", new JsonObject(), "IMAGE", null // set later
		);
	}

	@AfterEach
	void tearDown() {
		this.underTest.deleteAll();
		this.userRepository.deleteAll();
	}

	@Test
	void itShouldFindByName() {
		// Given
		this.file.setUser(this.user);
		this.underTest.save(this.file);

		// When
		File expected = this.underTest.findByName(this.file.getName());

		// Then
		assertThat(expected)
				.isNotNull()
				.usingRecursiveComparison(TEST_COMPARISON_CONFIG)
				.isEqualTo(this.file);
	}

	@Test
	void itShouldNotFindByName() {
		// Given
		// When
		File expected = this.underTest.findByName(this.file.getName());

		// Then
		assertThat(expected).isNull();
	}
}
