package rs.chat.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import rs.chat.domain.entity.UserChat;
import rs.chat.domain.entity.UserChatPK;

import java.util.List;

public interface UserChatRepository extends JpaRepository<UserChat, UserChatPK> {
	List<UserChat> findAllByUserChatPK_UserId(Long userId);
}
