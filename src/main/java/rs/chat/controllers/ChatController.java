package rs.chat.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import rs.chat.net.HttpRequest;
import rs.chat.net.HttpResponse;
import rs.chat.router.Routes;

@Slf4j
@RestController
@RequiredArgsConstructor
public class ChatController {
	@GetMapping(Routes.CHAT_METADATA_URL)
	public void getChatMetadata(HttpRequest request,
	                            HttpResponse response,
	                            @PathVariable String type,
	                            @PathVariable String id) {
		//
	}

	@GetMapping(Routes.CHAT_CONTENT_URL)
	public void getChatContent(HttpRequest request,
	                           HttpResponse response,
	                           @PathVariable String type,
	                           @PathVariable String id) {
		//
	}
}
