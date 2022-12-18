package rs.chat.domain.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rs.chat.domain.entity.Subject;
import rs.chat.domain.entity.TeaSubj;
import rs.chat.domain.entity.TeaSubjPK;
import rs.chat.domain.entity.User;
import rs.chat.domain.repository.SubjectRepository;
import rs.chat.domain.repository.TeacherSubjectRepository;
import rs.chat.domain.repository.UserRepository;

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

	public List<Subject> getSubjects(Long id) {
		return this.teacherSubjectRepository.findAllByTeaSubjPK_TeacherId(id)
		                                    .stream()
		                                    .map(TeaSubj::getTeaSubjPK)
		                                    .map(TeaSubjPK::getSubjectId)
		                                    .map(this.subjectRepository::findById)
		                                    .filter(Optional::isPresent)
		                                    .map(Optional::get)
		                                    .toList();
	}

	public List<User> getTeachers() {
		return this.teacherSubjectRepository.findAll()
		                                    .stream()
		                                    .map(TeaSubj::getTeaSubjPK)
		                                    .map(TeaSubjPK::getTeacherId)
		                                    .map(this.userRepository::findById)
		                                    .filter(Optional::isPresent)
		                                    .map(Optional::get)
		                                    .map(this::clearPassword)
		                                    .toList();
	}

	private User clearPassword(User user) {
		user.setPassword(null);
		return user;
	}
}
