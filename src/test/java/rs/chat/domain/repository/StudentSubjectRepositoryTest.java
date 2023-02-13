package rs.chat.domain.repository;

import com.google.gson.JsonObject;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import rs.chat.domain.entity.Degree;
import rs.chat.domain.entity.StuSubj;
import rs.chat.domain.entity.StuSubjId;
import rs.chat.domain.entity.Subject;
import rs.chat.domain.entity.User;
import rs.chat.utils.Constants;

import java.util.LinkedHashSet;
import java.util.Set;

import static java.util.Collections.emptySet;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static rs.chat.Constants.TEST_COMPARISON_CONFIG;

@DataJpaTest
class StudentSubjectRepositoryTest {
	@Autowired private StudentSubjectRepository underTest;
	@Autowired private UserRepository userRepository;
	@Autowired private SubjectRepository subjectRepository;
	@Autowired private DegreeRepository degreeRepository;
	private User student;
	private User teacher;
	private Degree degree;
	private Subject subject;
	private StuSubj stuSubj;
	private StuSubjId stuSubjId;

	private void initEntities() {
		this.student = this.userRepository.save(new User(
				1L, "david", "12345", "david@hello.com",
				"David Gar Dom", (byte) 21, null, Constants.STUDENT_ROLE,
				null, null, new JsonObject(), emptySet(),
				emptySet(), emptySet(), emptySet(), emptySet(),
				emptySet(), emptySet()
		));

		this.teacher = this.userRepository.save(new User(
				2L, "theBoss", "12345", "boss@hello.com",
				"Manuel Ferrero Gar", (byte) 37, null, Constants.TEACHER_ROLE,
				null, null, new JsonObject(), emptySet(),
				emptySet(), emptySet(), emptySet(), emptySet(),
				emptySet(), emptySet()
		));

		this.degree = this.degreeRepository.save(new Degree(
				1L, "Computer Science", new LinkedHashSet<>()
		));

		this.subject = this.subjectRepository.save(new Subject(
				1L, "Math", "S1", "FB", (byte) 6, (byte) 1, this.degree,
				Set.of(this.student), Set.of(this.teacher)
		));

		this.subject.setDegree(this.degree);
		this.subjectRepository.save(this.subject); // Add the subject to the degree

		this.stuSubjId = new StuSubjId(this.student.getId(), this.subject.getId());
		this.stuSubj = new StuSubj(this.stuSubjId, this.student, this.subject);
	}

	@BeforeEach
	void setUp() {
		this.initEntities();
	}

	@AfterEach
	void tearDown() {
		this.underTest.deleteAll();
		this.userRepository.deleteAll();
		this.subjectRepository.deleteAll();
		this.degreeRepository.deleteAll();
	}

	@Test
	void itShouldDeleteAllById_SubjectId() {
		// Given
		this.underTest.save(this.stuSubj);

		// When (here we deviate from the standard test structure)
		assertThat(this.underTest.findById(this.stuSubjId))
				.isPresent()
				.get()
				.usingRecursiveComparison(TEST_COMPARISON_CONFIG)
				.isEqualTo(this.stuSubj);
		this.underTest.deleteAllById_SubjectId(this.subject.getId());

		// Then
		assertThat(this.underTest.existsById(this.stuSubjId)).isFalse();
	}

	@Test
	void itShouldNotDeleteAllById_SubjectId() {
		// Given
		this.underTest.save(this.stuSubj);

		// When (here we deviate from the standard test structure)
		assertThat(this.underTest.findById(this.stuSubjId))
				.isPresent()
				.get()
				.usingRecursiveComparison(TEST_COMPARISON_CONFIG)
				.isEqualTo(this.stuSubj);
		this.underTest.deleteAllById_SubjectId(this.subject.getId() + 1);

		// Then
		assertThat(this.underTest.existsById(this.stuSubjId)).isTrue();
	}
}
