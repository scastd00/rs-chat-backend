package rs.chat.controllers;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import rs.chat.domain.entity.Chat;
import rs.chat.domain.entity.User;
import rs.chat.net.http.HttpResponse;
import rs.chat.service.ChatService;
import rs.chat.service.UserService;

import java.io.IOException;
import java.util.List;

import static rs.chat.router.Routes.GetRoute.ALL_CHATS_OF_USER_URL;

@Slf4j
@RestController
@RequiredArgsConstructor
public class ChatController {
	private final UserService userService;
	private final ChatService chatService;

	@GetMapping(ALL_CHATS_OF_USER_URL)
	public void getAllChatsOfUserDividedByType(HttpResponse response,
	                                           @PathVariable String username) throws IOException {
		User user = this.userService.getUser(username);

		if (user == null) {
			throw new UsernameNotFoundException("Username '%s' was not found".formatted(username));
		}

		Long userId = user.getId();
		List<Chat> allChatsOfUser = this.chatService.getAllChatsOfUser(userId);
		JsonObject allChats = new JsonObject();

		allChatsOfUser.forEach(chat -> {
			JsonObject chatItem = new JsonObject();
			chatItem.addProperty("id", chat.getId());
			chatItem.addProperty("name", chat.getName());

			if (!allChats.has(chat.getType())) {
				JsonArray typeArray = new JsonArray();
				typeArray.add(chatItem);

				allChats.add(chat.getType(), typeArray);
			} else {
				JsonArray array = (JsonArray) allChats.get(chat.getType());
				array.add(chatItem);
			}
		});

		response.ok().send("chats", allChats.toString());
	}
}
