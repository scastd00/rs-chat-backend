package rs.chat.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import rs.chat.domain.entity.UserBadge;
import rs.chat.domain.entity.UserBadgeId;

@SuppressWarnings("java:S100")
public interface UserBadgeRepository extends JpaRepository<UserBadge, UserBadgeId> {
	boolean existsById_UserIdAndId_BadgeId(Long userId, Long badgeId);
}
