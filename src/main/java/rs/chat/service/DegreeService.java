package rs.chat.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rs.chat.domain.Degree;
import rs.chat.domain.DomainUtils;
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

	public List<Degree> getDegrees() {
		return this.degreeRepository.findAll(Sort.by("name"));
	}

	public Degree getByName(String name) {
		return this.degreeRepository.findByName(name);
	}

	public Degree saveDegree(Degree degree) {
		Degree savedDegree = this.degreeRepository.save(degree);

		// When I know that the degree is saved, chat is created.
		this.chatRepository.save(DomainUtils.degreeChat(degree.getName()));
		return savedDegree;
	}

	public boolean existsDegree(String degreeName) {
		return this.getByName(degreeName) != null;
	}

	public Degree changeDegreeName(String oldName, String newName) {
		Degree degree = this.degreeRepository.findByName(oldName);
		degree.setName(newName);
		return this.degreeRepository.save(degree);
	}

	public void deleteDegreeByName(String degreeName) {
		Degree degree = this.degreeRepository.findByName(degreeName);
		this.degreeRepository.delete(degree);
		this.chatRepository.deleteByName(DEGREE_CHAT_S3_FOLDER_PREFIX + degreeName);
	}
}
