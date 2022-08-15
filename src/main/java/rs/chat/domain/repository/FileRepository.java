package rs.chat.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import rs.chat.domain.entity.File;

public interface FileRepository extends JpaRepository<File, Long> {
	File findByName(String name);
}
