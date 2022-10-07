package rs.chat.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import rs.chat.domain.entity.Group;

import java.util.Optional;

public interface GroupRepository extends JpaRepository<Group, Long> {
	Optional<Group> findByName(String name);
}
