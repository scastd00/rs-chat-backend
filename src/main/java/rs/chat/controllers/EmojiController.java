package rs.chat.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import rs.chat.domain.entity.dtos.EmojiDto;
import rs.chat.domain.service.EmojiService;
import rs.chat.net.http.HttpResponse;

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
		response.ok().send(this.emojiService.getRandomEmojis(count));
	}

	@GetMapping(EMOJI_STARTING_WITH_STRING_URL)
	public void getEmojisStartingWithString(HttpResponse response, @PathVariable String string) throws IOException {
		if (string.length() == 0) {
			response.ok().send(List.of());
			return; // The user has not typed anything yet
		}

		List<EmojiDto> emojiDTOs = this.emojiService.getEmojisStartingWith(string);

		if (emojiDTOs.isEmpty()) {
			response.notFound().send("No emojis found");
			return;
		}

		response.ok().send(emojiDTOs);
	}

	@GetMapping(EMOJI_BY_CATEGORY_URL)
	public void getEmojisByCategory(HttpResponse response, @PathVariable String category) throws IOException {
		List<EmojiDto> emojiDTOs = this.emojiService.getEmojisByCategory(category.replace("%20", " "));

		if (emojiDTOs.isEmpty()) {
			response.notFound().send("No emojis found");
			return;
		}

		response.ok().send(emojiDTOs);
	}

	@GetMapping(EMOJIS_GROUPED_BY_CATEGORY_URL)
	public void getEmojisGroupedByCategory(HttpResponse response) throws IOException {
		response.ok().send(this.emojiService.getEmojisGroupedByCategory());
	}
}
