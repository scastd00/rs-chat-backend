package rs.chat.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import rs.chat.domain.entity.Degree;

import java.util.Optional;

public interface DegreeRepository extends JpaRepository<Degree, Long> {
	Optional<Degree> findByName(String name);

	boolean existsByName(String name);
}
