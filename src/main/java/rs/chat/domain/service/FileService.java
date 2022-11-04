package rs.chat.domain.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rs.chat.domain.entity.File;
import rs.chat.domain.repository.FileRepository;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class FileService {
	private final FileRepository fileRepository;

	public void save(File file) {
		this.fileRepository.save(file);
	}
}
