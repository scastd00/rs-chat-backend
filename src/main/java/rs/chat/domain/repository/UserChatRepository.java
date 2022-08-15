package rs.chat.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import rs.chat.domain.entity.UserChat;

import java.util.List;

public interface UserChatRepository extends JpaRepository<UserChat, Long> {
	List<UserChat> findAllByUserId(Long userId);
}
