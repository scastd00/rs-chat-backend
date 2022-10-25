package rs.chat.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import rs.chat.domain.entity.Emoji;
import rs.chat.exceptions.NotFoundException;
import rs.chat.net.http.HttpResponse;
import rs.chat.service.EmojiService;

import java.io.IOException;
import java.util.List;

import static rs.chat.router.Routes.GetRoute.EMOJIS_GROUPED_BY_CATEGORY_URL;
import static rs.chat.router.Routes.GetRoute.EMOJI_BY_CATEGORY_URL;
import static rs.chat.router.Routes.GetRoute.EMOJI_STARTING_WITH_STRING_URL;
import static rs.chat.router.Routes.GetRoute.RANDOM_EMOJIS_URL;

/**
 * Controller that manages all subject-related requests.
 */
@Slf4j
@RestController
@RequiredArgsConstructor
public class EmojiController {
	private final EmojiService emojiService;

	@GetMapping(RANDOM_EMOJIS_URL)
	public void getRandomEmojis(HttpResponse response, @PathVariable Long count) throws IOException {
		response.status(HttpStatus.OK).send("emojis", this.emojiService.getRandomEmojis(count));
	}

	@GetMapping(EMOJI_STARTING_WITH_STRING_URL)
	public void getEmojisStartingWithString(HttpResponse response, @PathVariable String string) throws IOException {
		if (string.length() == 0) {
			response.status(HttpStatus.OK).send("emojis", List.of());
			return; // The user has not typed anything yet
		}

		List<Emoji> emojis = this.emojiService.getEmojisStartingWith(string);

		if (emojis.isEmpty()) {
			throw new NotFoundException("No emojis found");
		}

		response.status(HttpStatus.OK).send("emojis", emojis);
	}

	@GetMapping(EMOJI_BY_CATEGORY_URL)
	public void getEmojisByCategory(HttpResponse response, @PathVariable String category) throws IOException {
		List<Emoji> emojis = this.emojiService.getEmojisByCategory(category.replace("%20", " "));

		if (emojis.isEmpty()) {
			throw new NotFoundException("No emojis found");
		}

		response.status(HttpStatus.OK).send("emojis", emojis);
	}

	@GetMapping(EMOJIS_GROUPED_BY_CATEGORY_URL)
	public void getEmojisGroupedByCategory(HttpResponse response) throws IOException {
		response.status(HttpStatus.OK).send("emojis", this.emojiService.getEmojisGroupedByCategory());
	}
}
