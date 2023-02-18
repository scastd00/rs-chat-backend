package rs.chat.unit.domain.repository;

import org.assertj.core.api.AbstractListAssert;
import org.assertj.core.api.ObjectAssert;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import rs.chat.domain.entity.Degree;
import rs.chat.domain.entity.Subject;
import rs.chat.domain.entity.TeaSubj;
import rs.chat.domain.entity.TeaSubjId;
import rs.chat.domain.entity.User;
import rs.chat.domain.repository.DegreeRepository;
import rs.chat.domain.repository.SubjectRepository;
import rs.chat.domain.repository.TeacherSubjectRepository;
import rs.chat.domain.repository.UserRepository;
import rs.chat.utils.Constants;

import java.util.LinkedHashSet;
import java.util.List;

import static java.util.Collections.emptySet;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static rs.chat.Constants.TEST_COMPARISON_CONFIG;
import static rs.chat.TestUtils.createUserWithRole;

@DataJpaTest
class TeacherSubjectRepositoryTest {

	@Autowired private TeacherSubjectRepository underTest;
	@Autowired private UserRepository userRepository;
	@Autowired private SubjectRepository subjectRepository;
	@Autowired private DegreeRepository degreeRepository;
	private User teacher1;
	private User teacher2;
	private Degree degree;
	private Subject subject;
	private TeaSubj teaSubj1;
	private TeaSubjId teaSubjId1;
	private TeaSubj teaSubj2;
	private TeaSubjId teaSubjId2;

	private void initEntities() {
		this.teacher1 = this.userRepository.save(createUserWithRole(Constants.TEACHER_ROLE));

		this.teacher2 = this.userRepository.save(createUserWithRole(Constants.TEACHER_ROLE));

		this.degree = this.degreeRepository.save(new Degree(
				null, "Computer Science", new LinkedHashSet<>()
		));

		this.subject = this.subjectRepository.save(new Subject(
				null, "Math", "S1", "FB", (byte) 6, (byte) 1, this.degree,
				emptySet(), emptySet()
		));

		this.teaSubjId1 = new TeaSubjId(this.teacher1.getId(), this.subject.getId());
		this.teaSubj1 = new TeaSubj(this.teaSubjId1, this.teacher1, this.subject);

		this.teaSubjId2 = new TeaSubjId(this.teacher2.getId(), this.subject.getId());
		this.teaSubj2 = new TeaSubj(this.teaSubjId2, this.teacher2, this.subject);
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
	void itShouldFindAllById_TeacherId() {
		// Given
		this.underTest.save(this.teaSubj1);

		// When
		List<TeaSubj> teaSubjs = this.underTest.findAllById_TeacherId(this.teacher1.getId());

		// Then
		assertThat(teaSubjs)
				.asList()
				.singleElement()
				.usingRecursiveComparison(TEST_COMPARISON_CONFIG)
				.isEqualTo(this.teaSubj1);
	}

	@Test
	void itShouldNotFindAllById_TeacherId() {
		// Given
		// When
		List<TeaSubj> teaSubjs = this.underTest.findAllById_TeacherId(this.teacher1.getId());

		// Then
		assertThat(teaSubjs).asList().isEmpty();
	}

	@Test
	void itShouldDeleteAllById_SubjectId() {
		// Given
		this.underTest.save(this.teaSubj1);

		// When
		assertThat(this.underTest.findAll())
				.asList()
				.singleElement()
				.usingRecursiveComparison(TEST_COMPARISON_CONFIG)
				.isEqualTo(this.teaSubj1);
		this.underTest.deleteAllById_SubjectId(this.subject.getId());

		// Then
		assertThat(this.underTest.findAll()).asList().isEmpty();
	}

	@Test
	void itShouldDeleteAllById_SubjectIdMulti() {
		// Given
		this.underTest.save(this.teaSubj1);
		this.underTest.save(this.teaSubj2);

		AbstractListAssert<?, List<?>, Object, ObjectAssert<Object>> listAssert =
				assertThat(this.underTest.findAll())
						.asList()
						.hasSize(2);
		listAssert
				.first()
				.usingRecursiveComparison(TEST_COMPARISON_CONFIG)
				.isEqualTo(this.teaSubj1);
		listAssert
				.last()
				.usingRecursiveComparison(TEST_COMPARISON_CONFIG)
				.isEqualTo(this.teaSubj2);

		// When
		this.underTest.deleteAllById_SubjectId(this.subject.getId());

		// Then
		assertThat(this.underTest.findAll()).asList().isEmpty();
	}

	@Test
	void itShouldNotDeleteAllById_SubjectId() {
		// Given
		this.underTest.save(this.teaSubj1);

		// When
		assertThat(this.underTest.findAll())
				.asList()
				.singleElement()
				.usingRecursiveComparison(TEST_COMPARISON_CONFIG)
				.isEqualTo(this.teaSubj1);
		this.underTest.deleteAllById_SubjectId(this.subject.getId() + 1);

		// Then
		assertThat(this.underTest.findAll())
				.asList()
				.singleElement()
				.usingRecursiveComparison(TEST_COMPARISON_CONFIG)
				.isEqualTo(this.teaSubj1);
	}

	@Test
	void itShouldNotDeleteAllById_SubjectIdMulti() {
		// Given
		this.underTest.save(this.teaSubj1);
		this.underTest.save(this.teaSubj2);

		AbstractListAssert<?, List<?>, Object, ObjectAssert<Object>> listAssert =
				assertThat(this.underTest.findAll())
						.asList()
						.hasSize(2);
		listAssert
				.first()
				.usingRecursiveComparison(TEST_COMPARISON_CONFIG)
				.isEqualTo(this.teaSubj1);
		listAssert
				.last()
				.usingRecursiveComparison(TEST_COMPARISON_CONFIG)
				.isEqualTo(this.teaSubj2);

		// When
		this.underTest.deleteAllById_SubjectId(this.subject.getId() + 1);

		// Then
		listAssert = assertThat(this.underTest.findAll())
				.asList()
				.hasSize(2);
		listAssert
				.first()
				.usingRecursiveComparison(TEST_COMPARISON_CONFIG)
				.isEqualTo(this.teaSubj1);
		listAssert
				.last()
				.usingRecursiveComparison(TEST_COMPARISON_CONFIG)
				.isEqualTo(this.teaSubj2);
	}

	@Test
	void itShouldExistsById_TeacherIdAndId_SubjectId() {
		// Given
		this.underTest.save(this.teaSubj1);

		// When
		boolean exists = this.underTest.existsById_TeacherIdAndId_SubjectId(this.teacher1.getId(), this.subject.getId());

		// Then
		assertThat(exists).isTrue();
	}

	@Test
	void itShouldNotExistsById_TeacherIdAndId_SubjectId() {
		// Given
		// When
		boolean exists = this.underTest.existsById_TeacherIdAndId_SubjectId(this.teacher1.getId(), this.subject.getId());

		// Then
		assertThat(exists).isFalse();
	}
}
