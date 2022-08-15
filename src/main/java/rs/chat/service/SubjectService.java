package rs.chat.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rs.chat.domain.DomainUtils;
import rs.chat.domain.entity.Subject;
import rs.chat.domain.repository.ChatRepository;
import rs.chat.domain.repository.SubjectRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class SubjectService {
	private final SubjectRepository subjectRepository;
	private final ChatRepository chatRepository;

	public List<Subject> getAll() {
		return this.subjectRepository.findAll();
	}

	public Subject getByName(String subjectName) {
		return this.subjectRepository.findByName(subjectName);
	}

	public boolean exists(String subjectName) {
		return this.getByName(subjectName) != null;
	}

	public Subject save(Subject subject) {
		Subject savedSubject = this.subjectRepository.save(subject);

		// When I know that the subject is saved, chat is created.
		this.chatRepository.save(DomainUtils.subjectChat(subject.getName()));
		return savedSubject;
	}
}
