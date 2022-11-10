package rs.chat.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import rs.chat.domain.entity.Chat;

import java.util.Optional;

public interface ChatRepository extends JpaRepository<Chat, Long> {
	Optional<Chat> findByName(String name);

	Optional<Chat> findByInvitationCode(String invitationCode);

	Optional<Chat> findByKey(String key);
}
