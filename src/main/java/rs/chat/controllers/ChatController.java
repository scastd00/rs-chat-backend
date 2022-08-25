package rs.chat.controllers;

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

import static rs.chat.net.http.HttpResponse.HttpResponseBody;
import static rs.chat.router.Routes.GetRoute.ALL_CHATS_OF_USER_URL;
import static rs.chat.router.Routes.GetRoute.CHAT_INFO_URL;

/**
 * Controller that manages all chat-related requests.
 */
@Slf4j
@RestController
@RequiredArgsConstructor
public class ChatController {
	private final UserService userService;
	private final ChatService chatService;

	/**
	 * Returns all chats of a user organized by chat type.
	 *
	 * @param response response object with the chats to which the user can connect.
	 * @param username username of the user whose chats are to be returned.
	 *
	 * @throws IOException if an error occurs while sending the response back to the client.
	 */
	@GetMapping(ALL_CHATS_OF_USER_URL)
	public void getAllChatsOfUserDividedByType(HttpResponse response,
	                                           @PathVariable String username) throws IOException {
		User user = this.userService.getUser(username);

		if (user == null) {
			throw new UsernameNotFoundException("Username '%s' was not found".formatted(username));
		}

		response.ok().send(
				"chats",
				this.chatService.getAllChatsOfUserGroupedByType(user.getId())
		);
	}

	/**
	 * Returns information about a chat.
	 *
	 * @param response response object that contains the name and metadata information
	 *                 of the chat.
	 * @param id       id of the chat to be returned information about.
	 *
	 * @throws IOException if an error occurs while sending the response back to the client.
	 */
	@GetMapping(CHAT_INFO_URL)
	public void getChatInformation(HttpResponse response, @PathVariable String id) throws IOException {
		Chat chat = this.chatService.getChatById(Long.parseLong(id));

		HttpResponseBody body = new HttpResponseBody("name", chat.getName());
		body.add("metadata", chat.getMetadata());

		response.ok().send(body);
	}
}
