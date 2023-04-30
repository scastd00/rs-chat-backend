package rs.chat.domain.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rs.chat.domain.DomainUtils;
import rs.chat.domain.entity.Chat;
import rs.chat.domain.entity.Degree;
import rs.chat.domain.repository.ChatRepository;
import rs.chat.domain.repository.DegreeRepository;
import rs.chat.domain.repository.StudentSubjectRepository;
import rs.chat.domain.repository.SubjectRepository;
import rs.chat.domain.repository.TeacherSubjectRepository;
import rs.chat.domain.repository.UserChatRepository;
import rs.chat.exceptions.BadRequestException;
import rs.chat.exceptions.NotFoundException;
import rs.chat.storage.S3;

import java.util.List;

import static rs.chat.Constants.CHAT_KEY_FORMAT;
import static rs.chat.Constants.DEGREE;
import static rs.chat.Constants.SUBJECT;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class DegreeService {
	private final DegreeRepository degreeRepository;
	private final ChatRepository chatRepository;
	private final UserChatRepository userChatRepository;
	private final SubjectRepository subjectRepository;
	private final StudentSubjectRepository studentSubjectRepository;
	private final TeacherSubjectRepository teacherSubjectRepository;

	/**
	 * Finds all degrees.
	 *
	 * @return all degrees sorted by name in ascending order.
	 */
	public List<Degree> getDegrees() {
		return this.degreeRepository.findAll(Sort.by("name"));
	}

	/**
	 * Finds a degree by id.
	 *
	 * @param degreeId the id of the degree.
	 * @return the {@link Degree} with the given id.
	 */
	public Degree getById(Long degreeId) {
		return this.degreeRepository.findById(degreeId)
		                            .orElseThrow(() -> new NotFoundException("Degree with id %d not found.".formatted(degreeId)));
	}

	/**
	 * Finds a degree by name.
	 *
	 * @param name the name of the degree.
	 *
	 * @return the {@link Degree} with the given name.
	 */
	public Degree getByName(String name) {
		return this.degreeRepository.findByName(name)
		                            .orElseThrow(() -> new NotFoundException("Degree with name %s not found.".formatted(name)));
	}

	/**
	 * Saves a new degree to the database.
	 *
	 * @param degree the degree to save.
	 *
	 * @return the saved {@link Degree}.
	 */
	public Degree saveDegree(Degree degree) {
		if (this.existsDegree(degree.getName())) {
			throw new BadRequestException("Degree already exists: " + degree.getName());
		}

		Degree savedDegree = this.degreeRepository.save(degree);

		// When I know that the degree is saved, chat is created.
		this.chatRepository.save(DomainUtils.degreeChat(degree.getName(), savedDegree.getId()));
		return savedDegree;
	}

	/**
	 * Checks if a degree with the given name exists.
	 *
	 * @param degreeName the name of the degree.
	 *
	 * @return {@code true} if a degree with the given name exists, {@code false} otherwise.
	 */
	public boolean existsDegree(String degreeName) {
		return this.degreeRepository.existsByName(degreeName);
	}

	/**
	 * Changes the name of the given degree.
	 *
	 * @param oldName the old name of the degree.
	 * @param newName the new name of the degree.
	 *
	 * @return modified {@link Degree} object.
	 */
	public Degree changeDegreeName(String oldName, String newName) {
		Degree degree = this.getByName(oldName);

		if (degree.getName().equals(newName)) {
			return degree; // Do not modify
		}

		degree.setName(newName);
		return this.degreeRepository.save(degree);
	}

	public void deleteById(Long id) {
		if (!this.degreeRepository.existsById(id)) {
			throw new NotFoundException("Degree with id '%d' does not exist.".formatted(id));
		}

		String degreeChatKey = CHAT_KEY_FORMAT.formatted(DEGREE, id);
		Chat degreeChat = this.chatRepository.findByKey(degreeChatKey)
		                                     .orElseThrow(
				                                     () -> new NotFoundException("Chat for degree %s not found.".formatted(id))
		                                     );

		/*
		 * - Delete all students of every subject of the degree
		 * - Delete all teachers of every subject of the degree
		 * - Delete all subjects of the degree
		 *
		 * - Delete all user-chat associated with the subject chat
		 * - Delete chat of every subject
		 *
		 * - Delete the degree
		 * - Delete the user-chats associated with the degree chat
		 * - Delete the degree chat
		 */
		this.subjectRepository
				.findAllByDegreeId(id)
				.forEach(subject -> {
					String subjectChatKey = CHAT_KEY_FORMAT.formatted(SUBJECT, subject.getId());
					Chat subjectChat = this.chatRepository
							.findByKey(subjectChatKey)
							.orElseThrow(
									() -> new NotFoundException("Chat for subject %s not found.".formatted(subject.getId()))
							);
					this.studentSubjectRepository.deleteAllById_SubjectId(subject.getId());
					this.teacherSubjectRepository.deleteAllById_SubjectId(subject.getId());
					this.subjectRepository.deleteById(subject.getId());
					this.userChatRepository.deleteAllById_ChatId(subjectChat.getId());
					this.chatRepository.deleteById(subjectChat.getId());
					S3.getInstance().deleteHistoryFile(subjectChatKey);
				});

		this.degreeRepository.deleteById(id);
		this.userChatRepository.deleteAllById_ChatId(degreeChat.getId());
		this.chatRepository.deleteById(degreeChat.getId());
		S3.getInstance().deleteHistoryFile(DEGREE + "-" + id);
	}
}
