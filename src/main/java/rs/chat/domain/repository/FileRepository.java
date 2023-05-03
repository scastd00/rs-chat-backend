package rs.chat.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import rs.chat.domain.entity.File;

import java.util.Optional;

public interface FileRepository extends JpaRepository<File, Long> {
	Optional<File> findByName(String name);
}
