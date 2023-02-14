package rs.chat.domain.repository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import rs.chat.domain.entity.Emoji;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static rs.chat.Constants.TEST_COMPARISON_CONFIG;

@DataJpaTest
class EmojiRepositoryTest {

	@Autowired
	private EmojiRepository emojiRepository;
	private Emoji emoji;
	private final String name = "grinning_face";
	private final String category = "face";

	@BeforeEach
	void setUp() {
		this.emoji = new Emoji(null, name, "😀", "1F600", category, "smile");
	}

	@AfterEach
	void tearDown() {
		this.emojiRepository.deleteAll();
	}

	@Test
	void itShouldFindByNameStartingWith() {
		// given
		this.emojiRepository.save(this.emoji);

		// when
		List<Emoji> expected = this.emojiRepository.findByNameStartingWith(this.name.substring(0, 4));

		// then
		assertThat(expected)
				.asList()
				.singleElement()
				.usingRecursiveComparison(TEST_COMPARISON_CONFIG)
				.isEqualTo(this.emoji);
	}

	@Test
	void itShouldNotFindByNameStartingWith() {
		// given
		// when
		List<Emoji> expected = this.emojiRepository.findByNameStartingWith(this.name.substring(0, 4));

		// then
		assertThat(expected).asList().isEmpty();
	}

	@Test
	void itShouldFindEmojisByCategory() {
		// given
		this.emojiRepository.save(this.emoji);

		// when
		List<Emoji> expected = this.emojiRepository.findEmojisByCategory(this.category);

		// then
		assertThat(expected)
				.asList()
				.singleElement()
				.usingRecursiveComparison(TEST_COMPARISON_CONFIG)
				.isEqualTo(this.emoji);
	}

	@Test
	void itShouldNotFindEmojisByCategory() {
		// given
		// when
		List<Emoji> expected = this.emojiRepository.findEmojisByCategory(this.category);

		// then
		assertThat(expected).asList().isEmpty();
	}

	@Test
	void itShouldExistsByName() {
		// given
		this.emojiRepository.save(this.emoji);

		// when
		boolean expected = this.emojiRepository.existsByName(this.name);

		// then
		assertThat(expected).isTrue();
	}

	@Test
	void itShouldNotExistsByName() {
		// given
		// when
		boolean expected = this.emojiRepository.existsByName(this.name);

		// then
		assertThat(expected).isFalse();
	}

	@Test
	void itShouldSelectRandomEmojis() {
		// given
		this.emojiRepository.save(this.emoji);

		// when
		List<Emoji> expected = this.emojiRepository.selectRandomEmojis(1);

		// then
		assertThat(expected)
				.asList()
				.singleElement()
				.usingRecursiveComparison(TEST_COMPARISON_CONFIG)
				.isEqualTo(this.emoji);
	}

	@Test
	void itShouldSelectRandomEmojisIncreasedLimit() {
		// given
		this.emojiRepository.save(this.emoji);

		// when
		List<Emoji> expected = this.emojiRepository.selectRandomEmojis(100);

		// then
		assertThat(expected)
				.asList()
				.singleElement()
				.usingRecursiveComparison(TEST_COMPARISON_CONFIG)
				.isEqualTo(this.emoji);
	}

	@Test
	void itShouldNotSelectRandomEmojis() {
		// given
		this.emojiRepository.save(this.emoji);

		// when
		List<Emoji> expected = this.emojiRepository.selectRandomEmojis(0);

		// then
		assertThat(expected).asList().isEmpty();
	}
}
