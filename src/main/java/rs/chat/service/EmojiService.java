package rs.chat.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rs.chat.domain.entity.Emoji;
import rs.chat.domain.repository.EmojiRepository;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class EmojiService {
	private final EmojiRepository emojiRepository;

	public Emoji save(Emoji emoji) {
		return this.emojiRepository.save(emoji);
	}

	public boolean exists(String name) {
		return this.emojiRepository.existsByName(name);
	}

	public List<Emoji> getRandomEmojis(long numberOfEmojis) {
		return this.emojiRepository.selectRandomEmojis(numberOfEmojis);
	}

	public List<Emoji> getEmojisStartingWith(String string) {
		return this.emojiRepository.findByNameStartingWith(string);
	}

	public List<Emoji> getEmojisByCategory(String category) {
		return this.emojiRepository.findEmojisByCategory(category);
	}

	public Map<String, List<Emoji>> getEmojisGroupedByCategory() {
		List<Emoji> emojis = this.emojiRepository.findAll();

		return emojis.stream().collect(Collectors.groupingBy(Emoji::getCategory));
	}
}
