package rs.chat.unit.domain.repository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import rs.chat.domain.entity.Group;
import rs.chat.domain.repository.GroupRepository;

import java.util.Collections;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static rs.chat.utils.TestConstants.TEST_COMPARISON_CONFIG;

@DataJpaTest
class GroupRepositoryTest {

	@Autowired private GroupRepository underTest;
	private Group group;
	private final String name = "name";

	@BeforeEach
	void setUp() {
		this.group = new Group(null, this.name, Collections.emptySet());
	}

	@AfterEach
	void tearDown() {
		this.underTest.deleteAll();
	}

	@Test
	void itShouldFindByName() {
		// Given
		this.underTest.save(this.group);

		// When
		Optional<Group> expected = this.underTest.findByName(this.name);

		// Then
		assertThat(expected)
				.isPresent()
				.get()
				.usingRecursiveComparison(TEST_COMPARISON_CONFIG)
				.isEqualTo(this.group);
	}

	@Test
	void itShouldNotFindByName() {
		// Given
		// When
		Optional<Group> expected = this.underTest.findByName(this.name);

		// Then
		assertThat(expected).isNotPresent();
	}
}
