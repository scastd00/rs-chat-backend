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
import rs.chat.domain.entity.dtos.DegreeDto;
import rs.chat.domain.entity.dtos.SubjectDto;
import rs.chat.domain.entity.dtos.UserDto;
import rs.chat.domain.entity.mappers.DegreeMapper;
import rs.chat.domain.entity.mappers.SubjectMapper;
import rs.chat.domain.entity.mappers.UserMapper;
import rs.chat.domain.repository.DegreeRepository;
import rs.chat.domain.repository.SubjectRepository;
import rs.chat.domain.repository.TeacherSubjectRepository;
import rs.chat.domain.repository.UserRepository;
import rs.chat.exceptions.BadRequestException;
import rs.chat.exceptions.NotFoundException;
import rs.chat.utils.Constants;

import java.util.Collections;
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
	private final UserMapper userMapper;
	private final SubjectMapper subjectMapper;
	private final DegreeMapper degreeMapper;

	public List<SubjectDto> getSubjects(Long id) {
		return this.teacherSubjectRepository.findAllById_TeacherId(id)
		                                    .stream()
		                                    .map(TeaSubj::getSubject)
		                                    .map(this.subjectMapper::toDto)
		                                    .toList();
	}

	public List<DegreeDto> getDegrees(Long id) {
		return this.teacherSubjectRepository.findAllById_TeacherId(id)
		                                    .stream()
		                                    .map(TeaSubj::getSubject)
		                                    .map(Subject::getDegree)
		                                    .map(Degree::getId)
		                                    .distinct()
		                                    .map(this.degreeRepository::findById)
		                                    .filter(Optional::isPresent)
		                                    .map(Optional::get)
		                                    .map(this.degreeMapper::toDto)
		                                    .toList();
	}

	public List<UserDto> getTeachers() {
		return this.userRepository.findAllByRole(Constants.TEACHER_ROLE)
		                          .orElse(Collections.emptyList())
		                          .stream()
		                          .map(this.userMapper::toDto)
		                          .toList();
	}

	/**
	 * Adds a teacher to a subject. If the relation already exists, it will throw an
	 * {@link IllegalArgumentException} and will not add it. Otherwise, it will add it.
	 *
	 * @param teacherId The id of the teacher.
	 * @param subjectId The id of the subject.
	 */
	public void addTeacherToSubject(long teacherId, long subjectId) {
		boolean exists = this.teacherSubjectRepository.existsById_TeacherIdAndId_SubjectId(teacherId, subjectId);

		if (exists) {
			throw new BadRequestException("Teacher already teaches this subject");
		}

		User teacher = this.teacherRepository.findById(teacherId).orElseThrow(() -> new NotFoundException("Teacher not found"));
		Subject subject = this.subjectRepository.findById(subjectId).orElseThrow(() -> new NotFoundException("Subject not found"));

		// 2nd and 3rd parameters are needed. Otherwise, it will throw an exception.
		this.teacherSubjectRepository.save(
				new TeaSubj(new TeaSubjId(teacherId, subjectId), teacher, subject)
		);
	}
}
