package rs.chat.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import rs.chat.net.http.HttpResponse;
import rs.chat.service.EmojiService;

import java.io.IOException;

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
	public void getRandomEmojis(HttpResponse response) throws IOException {
		response.status(HttpStatus.OK).send("emojis", this.emojiService.getRandomEmojis(6));
	}
}
