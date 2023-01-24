package rs.chat.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import rs.chat.domain.entity.UserChat;
import rs.chat.domain.entity.UserChatId;

import java.util.List;

@SuppressWarnings("java:S100")
public interface UserChatRepository extends JpaRepository<UserChat, UserChatId> {
	List<UserChat> findAllById_UserId(Long userId);

	boolean existsById_UserIdAndId_ChatId(Long userId, Long chatId);

	List<UserChat> findAllById_ChatId(Long chatId);

	void deleteAllById_ChatId(Long chatId);

	void deleteById_UserIdAndId_ChatId(Long userId, Long chatId);
}
