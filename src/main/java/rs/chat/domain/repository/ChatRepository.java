package rs.chat.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import rs.chat.domain.Chat;

public interface ChatRepository extends JpaRepository<Chat, Long> {
	Chat findByName(String name);

	void deleteByName(String name);
}
