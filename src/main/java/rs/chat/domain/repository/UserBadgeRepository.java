package rs.chat.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import rs.chat.domain.entity.UserBadge;
import rs.chat.domain.entity.UserBadgeId;

public interface UserBadgeRepository extends JpaRepository<UserBadge, UserBadgeId> {
}
