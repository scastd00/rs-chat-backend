package rs.chat.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import rs.chat.domain.Degree;

public interface DegreeRepository extends JpaRepository<Degree, Long> {
	Degree findByName(String name);
}