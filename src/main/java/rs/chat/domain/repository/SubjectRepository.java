package rs.chat.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import rs.chat.domain.entity.Subject;

public interface SubjectRepository extends JpaRepository<Subject, Long> {
	Subject findByName(String name);
}
