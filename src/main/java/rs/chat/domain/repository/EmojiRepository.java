package rs.chat.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import rs.chat.domain.entity.Emoji;

import java.util.List;

public interface EmojiRepository extends JpaRepository<Emoji, Long> {
	Emoji findByName(String name);

	List<Emoji> findByNameStartingWith(String s);

	List<Emoji> findEmojisByCategory(String category);

	boolean existsByName(String name);
}
