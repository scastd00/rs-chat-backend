package rs.chat.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import rs.chat.domain.Group;

public interface GroupRepository extends JpaRepository<Group, Long> {
	Group findByName(String name);
}
