package rs.chat.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rs.chat.domain.entity.Emoji;
import rs.chat.domain.repository.EmojiRepository;

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
}
