package rs.chat.domain.repository;

import com.google.gson.JsonObject;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import rs.chat.domain.entity.File;
import rs.chat.domain.entity.User;
import rs.chat.utils.Constants;

import java.time.Instant;

import static java.util.Collections.emptySet;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static rs.chat.Constants.RECURSIVE_COMPARISON_CONFIGURATION;

@DataJpaTest
class FileRepositoryTest {

	@Autowired private FileRepository underTest;
	@Autowired private UserRepository userRepository;
	private File file;
	private User user;

	@BeforeEach
	void setUp() {
		this.user = new User(
				1L, "david", "12345", "david@hello.com",
				"David Gar Dom", (byte) 21, null, Constants.STUDENT_ROLE,
				null, "FNvb23", new JsonObject(), emptySet(),
				emptySet(), emptySet(), emptySet(), emptySet(),
				emptySet(), emptySet()
		);

		this.file = new File(
				1L, "name", Instant.now(), 1024,
				"/path/to/file", new JsonObject(), "IMAGE", null // set later
		);
	}

	@AfterEach
	void tearDown() {
		this.underTest.deleteAll();
		this.userRepository.deleteAll();
	}

	@Test
	void findByName() {
		// Given
		User save = this.userRepository.save(this.user);
		this.file.setUser(save);
		this.underTest.save(this.file);

		// When
		File expected = this.underTest.findByName(this.file.getName());

		// Then
		assertThat(expected)
				.isNotNull()
				.usingRecursiveComparison(RECURSIVE_COMPARISON_CONFIGURATION)
				.isEqualTo(this.file);
	}
}
