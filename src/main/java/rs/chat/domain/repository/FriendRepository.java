package rs.chat.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import rs.chat.domain.entity.Friend;
import rs.chat.domain.entity.FriendId;

public interface FriendRepository extends JpaRepository<Friend, FriendId> {
}
