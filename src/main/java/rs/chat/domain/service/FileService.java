package rs.chat.domain.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rs.chat.domain.entity.File;
import rs.chat.domain.entity.dtos.FileDto;
import rs.chat.domain.entity.mappers.FileMapper;
import rs.chat.domain.repository.FileRepository;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class FileService {
	private final FileRepository fileRepository;
	private final FileMapper fileMapper;

	public FileDto save(File file) {
		return this.fileMapper.toDto(this.fileRepository.save(file));
	}
}
