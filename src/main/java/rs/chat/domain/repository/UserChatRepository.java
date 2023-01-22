package rs.chat.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import rs.chat.domain.entity.UserChat;
import rs.chat.domain.entity.UserChatId;

import java.util.List;

@SuppressWarnings("java:S100")
public interface UserChatRepository extends JpaRepository<UserChat, UserChatId> {
	List<UserChat> findAllByUserChatPK_UserId(Long userId);

	boolean existsByUserChatPK_UserIdAndUserChatPK_ChatId(Long userId, Long chatId);

	List<UserChat> findAllByUserChatPK_ChatId(Long chatId);

	void deleteAllByUserChatPK_ChatId(Long chatId);

	void deleteByUserChatPK_UserIdAndUserChatPK_ChatId(Long userId, Long chatId);
}
