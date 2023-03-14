package rs.chat.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import rs.chat.domain.entity.BlockedUser;
import rs.chat.domain.entity.BlockedUserId;

public interface BlockedUserRepository extends JpaRepository<BlockedUser, BlockedUserId> {
}
