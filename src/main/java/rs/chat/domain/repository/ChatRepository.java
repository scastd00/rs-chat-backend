package rs.chat.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import rs.chat.domain.entity.Chat;

public interface ChatRepository extends JpaRepository<Chat, Long> {
	Chat findByName(String name);

	Chat findByInvitationCode(String invitationCode);

	void deleteByName(String name);
}
