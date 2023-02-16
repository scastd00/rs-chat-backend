package rs.chat.controllers;

import jakarta.servlet.http.HttpServletResponse;
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

import static java.util.Collections.emptyList;
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
	public void getRandomEmojis(HttpServletResponse response, @PathVariable Long count) throws IOException {
		HttpResponse.ok(response);
		HttpResponse.send(response, this.emojiService.getRandomEmojis(count));
	}

	@GetMapping(EMOJI_STARTING_WITH_STRING_URL)
	public void getEmojisStartingWithString(HttpServletResponse response, @PathVariable String string) throws IOException {
		if (string.length() == 0) {
			HttpResponse.ok(response);
			HttpResponse.send(response, emptyList());
			return; // The user has not typed anything yet
		}

		List<EmojiDto> emojiDTOs = this.emojiService.getEmojisStartingWith(string);

		if (emojiDTOs.isEmpty()) {
			HttpResponse.notFound(response);
			HttpResponse.send(response, "No emojis found");
			return;
		}

		HttpResponse.ok(response);
		HttpResponse.send(response, emojiDTOs);
	}

	@GetMapping(EMOJI_BY_CATEGORY_URL)
	public void getEmojisByCategory(HttpServletResponse response, @PathVariable String category) throws IOException {
		List<EmojiDto> emojiDTOs = this.emojiService.getEmojisByCategory(category.replace("%20", " "));

		if (emojiDTOs.isEmpty()) {
			HttpResponse.notFound(response);
			HttpResponse.send(response, "No emojis found");
			return;
		}

		HttpResponse.ok(response);
		HttpResponse.send(response, emojiDTOs);
	}

	@GetMapping(EMOJIS_GROUPED_BY_CATEGORY_URL)
	public void getEmojisGroupedByCategory(HttpServletResponse response) throws IOException {
		HttpResponse.ok(response);
		HttpResponse.send(response, this.emojiService.getEmojisGroupedByCategory());
	}
}
