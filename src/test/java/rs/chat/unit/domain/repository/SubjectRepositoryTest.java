package rs.chat.unit.domain.repository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import rs.chat.domain.entity.Degree;
import rs.chat.domain.entity.Subject;
import rs.chat.domain.repository.DegreeRepository;
import rs.chat.domain.repository.SubjectRepository;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;

import static java.util.Collections.emptySet;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static rs.chat.utils.TestConstants.TEST_COMPARISON_CONFIG;

@DataJpaTest
class SubjectRepositoryTest {
	@Autowired private SubjectRepository underTest;
	@Autowired private DegreeRepository degreeRepository;
	private Degree degree;
	private Subject subject;

	private void initEntities() {
		this.degree = this.degreeRepository.save(new Degree(
				null, "Computer Science", new LinkedHashSet<>()
		));

		this.subject = new Subject(
				null, "Math", "S1", "FB", (byte) 6, (byte) 1, this.degree,
				emptySet(), emptySet()
		);
	}

	@BeforeEach
	void setUp() {
		this.initEntities();
	}

	@AfterEach
	void tearDown() {
		this.underTest.deleteAll();
		this.degreeRepository.deleteAll();
	}

	@Test
	void itShouldFindByName() {
		// Given
		this.underTest.save(this.subject);

		// When
		Optional<Subject> expected = this.underTest.findByName(this.subject.getName());

		// Then
		assertThat(expected)
				.isPresent()
				.get()
				.usingRecursiveComparison(TEST_COMPARISON_CONFIG)
				.isEqualTo(this.subject);
	}

	@Test
	void itShouldNotFindByName() {
		// Given
		// When
		Optional<Subject> expected = this.underTest.findByName(this.subject.getName());

		// Then
		assertThat(expected).isNotPresent();
	}

	@Test
	void itShouldFindAllByDegreeId() {
		// Given
		this.underTest.save(this.subject);

		// When
		Iterable<Subject> expected = this.underTest.findAllByDegreeId(this.degree.getId());

		// Then
		assertThat(expected)
				.asList()
				.singleElement()
				.usingRecursiveComparison(TEST_COMPARISON_CONFIG)
				.isEqualTo(this.subject);
	}

	@Test
	void itShouldNotFindAllByDegreeId() {
		// Given
		// When
		List<Subject> expected = this.underTest.findAllByDegreeId(this.degree.getId());

		// Then
		assertThat(expected).asList().isEmpty();
	}
}
