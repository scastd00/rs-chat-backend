package rs.chat.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import rs.chat.domain.entity.Badge;

import java.util.List;

public interface BadgeRepository extends JpaRepository<Badge, Long> {
	List<Badge> findAllByTypeOrderByPointsOfTypeDesc(String type);
}
