package rs.chat.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import rs.chat.domain.entity.Badge;

public interface BadgeRepository extends JpaRepository<Badge, Long> {
}
