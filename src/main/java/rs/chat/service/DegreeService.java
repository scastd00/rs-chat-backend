package rs.chat.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rs.chat.domain.DomainUtils;
import rs.chat.domain.entity.Degree;
import rs.chat.domain.repository.ChatRepository;
import rs.chat.domain.repository.DegreeRepository;

import java.util.List;

import static rs.chat.utils.Constants.DEGREE_CHAT_S3_FOLDER_PREFIX;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class DegreeService {
	private final DegreeRepository degreeRepository;
	private final ChatRepository chatRepository;

	/**
	 * Finds all degrees.
	 *
	 * @return all degrees sorted by name in ascending order.
	 */
	public List<Degree> getDegrees() {
		return this.degreeRepository.findAll(Sort.by("name"));
	}

	/**
	 * Finds a degree by name.
	 *
	 * @param name the name of the degree.
	 *
	 * @return the {@link Degree} with the given name.
	 */
	public Degree getByName(String name) {
		return this.degreeRepository.findByName(name);
	}

	/**
	 * Saves a new degree to the database.
	 *
	 * @param degree the degree to save.
	 *
	 * @return the saved {@link Degree}.
	 */
	public Degree saveDegree(Degree degree) {
		Degree savedDegree = this.degreeRepository.save(degree);

		// When I know that the degree is saved, chat is created.
		this.chatRepository.save(DomainUtils.degreeChat(degree.getName()));
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
		return this.getByName(degreeName) != null;
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
		Degree degree = this.degreeRepository.findByName(oldName);
		degree.setName(newName);
		return this.degreeRepository.save(degree);
	}

	/**
	 * Deletes a degree given its name.
	 *
	 * @param degreeName the name of the degree to delete.
	 */
	public void deleteDegreeByName(String degreeName) {
		Degree degree = this.degreeRepository.findByName(degreeName);
		this.degreeRepository.delete(degree);
		this.chatRepository.deleteByName(DEGREE_CHAT_S3_FOLDER_PREFIX + degreeName);
	}
}
