package rs.chat.unit.domain.repository;

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
import rs.chat.domain.repository.DegreeRepository;
import rs.chat.domain.repository.StudentSubjectRepository;
import rs.chat.domain.repository.SubjectRepository;
import rs.chat.domain.repository.UserRepository;
import rs.chat.utils.Constants;
import rs.chat.utils.factories.DefaultFactory;

import static org.assertj.core.api.Assertions.assertThat;
import static rs.chat.utils.TestConstants.TEST_COMPARISON_CONFIG;

@DataJpaTest
class StudentSubjectRepositoryTest {
	@Autowired private StudentSubjectRepository underTest;
	@Autowired private UserRepository userRepository;
	@Autowired private SubjectRepository subjectRepository;
	@Autowired private DegreeRepository degreeRepository;
	private User student1;
	private User student2;
	private Degree degree;
	private Subject subject;
	private StuSubj stuSubj1;
	private StuSubjId stuSubjId1;
	private StuSubj stuSubj2;
	private StuSubjId stuSubjId2;

	private void initEntities() {
		this.student1 = this.userRepository.save(DefaultFactory.INSTANCE.createUser(null, Constants.STUDENT_ROLE));
		this.student2 = this.userRepository.save(DefaultFactory.INSTANCE.createUser(null, Constants.STUDENT_ROLE));
		this.degree = this.degreeRepository.save(DefaultFactory.INSTANCE.createDegree(null, "Computer Science"));
		this.subject = this.subjectRepository.save(DefaultFactory.INSTANCE.createSubject(null, "Math", "S1", "FB", this.degree));

		this.stuSubjId1 = new StuSubjId(this.student1.getId(), this.subject.getId());
		this.stuSubj1 = new StuSubj(this.stuSubjId1, this.student1, this.subject);

		this.stuSubjId2 = new StuSubjId(this.student2.getId(), this.subject.getId());
		this.stuSubj2 = new StuSubj(this.stuSubjId2, this.student2, this.subject);
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
		this.underTest.save(this.stuSubj1);

		// When (here we deviate from the standard test structure)
		// Test that it exists, before deleting
		assertThat(this.underTest.findById(this.stuSubjId1))
				.isPresent()
				.get()
				.usingRecursiveComparison(TEST_COMPARISON_CONFIG)
				.isEqualTo(this.stuSubj1);
		this.underTest.deleteAllById_SubjectId(this.subject.getId());

		// Then
		assertThat(this.underTest.existsById(this.stuSubjId1)).isFalse();
	}

	@Test
	void itShouldDeleteAllById_SubjectIdMulti() {
		// Given
		this.underTest.save(this.stuSubj1);
		this.underTest.save(this.stuSubj2);

		// When (here we deviate from the standard test structure)
		// Test that it exists, before deleting
		assertMultipleArePresent();
		this.underTest.deleteAllById_SubjectId(this.subject.getId());

		// Then
		assertThat(this.underTest.existsById(this.stuSubjId1)).isFalse();
		assertThat(this.underTest.existsById(this.stuSubjId2)).isFalse();
	}

	@Test
	void itShouldNotDeleteAllById_SubjectId() {
		// Given
		this.underTest.save(this.stuSubj1);

		// When (here we deviate from the standard test structure)
		assertThat(this.underTest.findById(this.stuSubjId1))
				.isPresent()
				.get()
				.usingRecursiveComparison(TEST_COMPARISON_CONFIG)
				.isEqualTo(this.stuSubj1);
		this.underTest.deleteAllById_SubjectId(this.subject.getId() + 1);

		// Then
		assertThat(this.underTest.existsById(this.stuSubjId1)).isTrue();
	}

	@Test
	void itShouldNotDeleteAllById_SubjectIdMulti() {
		// Given
		this.underTest.save(this.stuSubj1);
		this.underTest.save(this.stuSubj2);

		// When (here we deviate from the standard test structure)
		assertMultipleArePresent();
		this.underTest.deleteAllById_SubjectId(this.subject.getId() + 1);

		// Then
		assertThat(this.underTest.existsById(this.stuSubjId1)).isTrue();
		assertThat(this.underTest.existsById(this.stuSubjId2)).isTrue();
	}

	private void assertMultipleArePresent() {
		assertThat(this.underTest.findById(this.stuSubjId1))
				.isPresent()
				.get()
				.usingRecursiveComparison(TEST_COMPARISON_CONFIG)
				.isEqualTo(this.stuSubj1);
		assertThat(this.underTest.findById(this.stuSubjId2))
				.isPresent()
				.get()
				.usingRecursiveComparison(TEST_COMPARISON_CONFIG)
				.isEqualTo(this.stuSubj2);
	}
}
