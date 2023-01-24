package rs.chat.domain.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rs.chat.domain.entity.Emoji;
import rs.chat.domain.entity.dtos.EmojiDto;
import rs.chat.domain.entity.mappers.EmojiMapper;
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
	private final EmojiMapper emojiMapper;

	public Emoji save(Emoji emoji) {
		return this.emojiRepository.save(emoji);
	}

	public boolean exists(String name) {
		return this.emojiRepository.existsByName(name);
	}

	public List<EmojiDto> getRandomEmojis(long numberOfEmojis) {
		return this.emojiRepository.selectRandomEmojis(numberOfEmojis)
		                           .stream()
		                           .map(this.emojiMapper::toDto)
		                           .toList();
	}

	public List<EmojiDto> getEmojisStartingWith(String string) {
		return this.emojiRepository.findByNameStartingWith(string)
		                           .stream()
		                           .map(this.emojiMapper::toDto)
		                           .toList();
	}

	public List<EmojiDto> getEmojisByCategory(String category) {
		return this.emojiRepository.findEmojisByCategory(category)
		                           .stream()
		                           .map(emojiMapper::toDto)
		                           .toList();
	}

	public Map<String, List<EmojiDto>> getEmojisGroupedByCategory() {
		List<EmojiDto> emojis = this.emojiRepository.findAll()
		                                            .stream()
		                                            .map(emojiMapper::toDto)
		                                            .toList();

		return emojis.stream()
		             .collect(Collectors.groupingBy(EmojiDto::category));
	}
}
