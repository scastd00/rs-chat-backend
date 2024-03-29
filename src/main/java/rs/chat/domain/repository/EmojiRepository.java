package rs.chat.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import rs.chat.domain.entity.Emoji;

import java.util.List;

public interface EmojiRepository extends JpaRepository<Emoji, Long> {
	@Query(value = "SELECT * FROM emojis e WHERE e.name LIKE concat(?1, '%') LIMIT 15", nativeQuery = true)
	List<Emoji> findByNameStartingWith(String s);

	List<Emoji> findEmojisByCategory(String category);

	boolean existsByName(String name);

	@Query(value = "SELECT * FROM emojis ORDER BY rand() LIMIT ?1", nativeQuery = true)
	List<Emoji> selectRandomEmojis(long numberOfEmojis);
}
