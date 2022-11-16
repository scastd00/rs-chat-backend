package rs.chat.domain.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rs.chat.domain.DomainUtils;
import rs.chat.domain.entity.Chat;
import rs.chat.domain.entity.Subject;
import rs.chat.domain.repository.ChatRepository;
import rs.chat.domain.repository.StudentSubjectRepository;
import rs.chat.domain.repository.SubjectRepository;
import rs.chat.domain.repository.TeacherSubjectRepository;
import rs.chat.domain.repository.UserChatRepository;
import rs.chat.exceptions.NotFoundException;
import rs.chat.storage.S3;

import java.util.List;

import static rs.chat.utils.Constants.CHAT_KEY_FORMAT;
import static rs.chat.utils.Constants.SUBJECT_CHAT;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class SubjectService {
	private final SubjectRepository subjectRepository;
	private final ChatRepository chatRepository;
	private final UserChatRepository userChatRepository;
	private final StudentSubjectRepository studentSubjectRepository;
	private final TeacherSubjectRepository teacherSubjectRepository;

	/**
	 * Finds all subjects.
	 *
	 * @return list of subjects or empty list if no subjects found in database.
	 */
	public List<Subject> getAll() {
		return this.subjectRepository.findAll();
	}

	/**
	 * Finds subject by name.
	 *
	 * @param subjectName name of subject to find.
	 *
	 * @return found subject or null if no subject found.
	 */
	public Subject getByName(String subjectName) {
		return this.subjectRepository.findByName(subjectName)
		                             .orElseThrow(() -> new NotFoundException("Subject with name " + subjectName + " not found."));
	}

	/**
	 * Checks if a subject with given name exists.
	 *
	 * @param subjectName name of subject to check.
	 *
	 * @return true if subject exists, false otherwise.
	 */
	public boolean exists(String subjectName) {
		return this.subjectRepository.findByName(subjectName).isPresent();
	}

	/**
	 * Saves a new subject to database.
	 *
	 * @param subject subject to save.
	 *
	 * @return saved subject.
	 */
	public Subject save(Subject subject) {
		Subject savedSubject = this.subjectRepository.save(subject);

		// When I know that the subject is saved, chat is created.
		this.chatRepository.save(DomainUtils.subjectChat(subject.getName(), savedSubject.getId()));
		return savedSubject;
	}

	public void deleteById(Long id) {
		if (!this.subjectRepository.existsById(id)) {
			throw new NotFoundException("Subject with id '%d' does not exist.".formatted(id));
		}

		String chatKey = CHAT_KEY_FORMAT.formatted(SUBJECT_CHAT, id);
		Chat chat = this.chatRepository.findByKey(chatKey)
		                               .orElseThrow(
				                               () -> new NotFoundException("Chat for subject %s not found.".formatted(id))
		                               );

		this.userChatRepository.deleteAllByUserChatPK_ChatId(chat.getId());
		this.chatRepository.deleteById(chat.getId());
		this.studentSubjectRepository.deleteAllByStuSubjPK_SubjectId(id);
		this.teacherSubjectRepository.deleteAllByTeaSubjPK_SubjectId(id);
		this.subjectRepository.deleteById(id);
		S3.getInstance().deleteHistoryFile(chatKey);
	}
}
