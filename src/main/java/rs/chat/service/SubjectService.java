package rs.chat.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rs.chat.domain.DomainUtils;
import rs.chat.domain.entity.Subject;
import rs.chat.domain.repository.ChatRepository;
import rs.chat.domain.repository.SubjectRepository;
import rs.chat.exceptions.NotFoundException;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class SubjectService {
	private final SubjectRepository subjectRepository;
	private final ChatRepository chatRepository;

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
		return this.getByName(subjectName) != null;
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
		this.chatRepository.save(DomainUtils.subjectChat(subject.getName()));
		return savedSubject;
	}
}
