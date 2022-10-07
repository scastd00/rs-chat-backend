package rs.chat.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import rs.chat.domain.entity.Subject;

import java.util.Optional;

public interface SubjectRepository extends JpaRepository<Subject, Long> {
	Optional<Subject> findByName(String name);
}
