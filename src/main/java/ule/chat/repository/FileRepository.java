package ule.chat.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ule.chat.domain.File;

public interface FileRepository extends JpaRepository<File, Long> {
	File findByName(String name);
}
