package ule.chat.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ule.chat.domain.Subject;

public interface SubjectRepository extends JpaRepository<Subject, Long> {
	Subject findByName(String name);
}
