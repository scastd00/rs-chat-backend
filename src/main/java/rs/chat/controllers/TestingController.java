package rs.chat.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import rs.chat.domain.entity.Chat;
import rs.chat.domain.entity.mappers.ChatMapper;
import rs.chat.domain.service.ChatService;
import rs.chat.net.http.HttpRequest;
import rs.chat.net.http.HttpResponse;

import java.io.IOException;

import static rs.chat.router.Routes.TEST_URL;

/**
 * Controller that manages all testing-related requests.
 */
@Slf4j
@RestController
@RequiredArgsConstructor
public class TestingController {
	private final ChatService chatService;
	private final ChatMapper chatMapper;

	@GetMapping(TEST_URL)
	public void getDto(HttpRequest request, HttpResponse response) throws IOException {
		Chat chatById = this.chatService.getChatById(1L);
		response.ok().send("dto", this.chatMapper.toDto(chatById));
	}
}
