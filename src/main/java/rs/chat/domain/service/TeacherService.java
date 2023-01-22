package rs.chat.domain.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rs.chat.domain.entity.Degree;
import rs.chat.domain.entity.Subject;
import rs.chat.domain.entity.TeaSubj;
import rs.chat.domain.entity.TeaSubjId;
import rs.chat.domain.entity.User;
import rs.chat.domain.repository.DegreeRepository;
import rs.chat.domain.repository.SubjectRepository;
import rs.chat.domain.repository.TeacherSubjectRepository;
import rs.chat.domain.repository.UserRepository;
import rs.chat.exceptions.BadRequestException;
import rs.chat.exceptions.NotFoundException;
import rs.chat.utils.Constants;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class TeacherService {
	private final TeacherSubjectRepository teacherSubjectRepository;
	private final SubjectRepository subjectRepository;
	private final UserRepository userRepository;
	private final UserRepository teacherRepository;
	private final DegreeRepository degreeRepository;

	public List<Subject> getSubjects(Long id) {
		return this.teacherSubjectRepository.findAllByTeaSubjPK_TeacherId(id)
		                                    .stream()
		                                    .map(TeaSubj::getId)
		                                    .map(TeaSubjId::getSubjectId)
		                                    .map(this.subjectRepository::findById)
		                                    .filter(Optional::isPresent)
		                                    .map(Optional::get)
		                                    .toList();
	}

	public List<Degree> getDegrees(Long id) {
		List<Long> degreeIds = this.teacherSubjectRepository.findAllByTeaSubjPK_TeacherId(id)
		                                                    .stream()
		                                                    .map(TeaSubj::getId)
		                                                    .map(TeaSubjId::getSubjectId)
		                                                    .map(this.subjectRepository::findById)
		                                                    .filter(Optional::isPresent)
		                                                    .map(Optional::get)
		                                                    .map(Subject::getDegree)
		                                                    .map(Degree::getId)
		                                                    .distinct()
		                                                    .toList();
		return this.degreeRepository.findAllById(degreeIds);
	}

	public List<User> getTeachers() {
		return this.userRepository.findAllByRole(Constants.TEACHER_ROLE)
		                          .orElseThrow(() -> new NotFoundException("No teachers found"));
	}

	/**
	 * Adds a teacher to a subject. If the relation already exists, it will throw an
	 * {@link IllegalArgumentException} and will not add it. Otherwise, it will add it.
	 *
	 * @param teacherId The id of the teacher.
	 * @param subjectId The id of the subject.
	 */
	public void addTeacherToSubject(long teacherId, long subjectId) {
		boolean exists = this.teacherSubjectRepository.existsByTeaSubjPK_TeacherIdAndTeaSubjPK_SubjectId(teacherId, subjectId);

		if (exists) {
			throw new BadRequestException("Teacher already teaches this subject");
		}

		User teacher = this.teacherRepository.findById(teacherId).orElseThrow(() -> new NotFoundException("Teacher not found"));
		Subject subject = this.subjectRepository.findById(subjectId).orElseThrow(() -> new NotFoundException("Subject not found"));

		this.teacherSubjectRepository.save(
				new TeaSubj(new TeaSubjId(teacherId, subjectId), teacher, subject)
		);
	}
}
