package rs.chat.controllers;

import com.google.gson.JsonObject;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import rs.chat.net.HttpRequest;
import rs.chat.net.HttpResponse;
import rs.chat.router.Routes;
import rs.chat.utils.ChatFiles;

import java.io.IOException;

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

	@PostMapping(Routes.CHAT_SEND_TEXT_MESSAGE_URL)
	public void sendTextMessage(HttpRequest request,
	                            HttpResponse response,
	                            @PathVariable String chatId) throws IOException {
		JsonObject body = request.body();
		log.info(body.toString());
		ChatFiles.writeMessage(body.toString());
	}
}
