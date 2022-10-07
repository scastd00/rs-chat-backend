package rs.chat.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import rs.chat.domain.entity.Chat;
import rs.chat.domain.entity.User;
import rs.chat.exceptions.BadRequestException;
import rs.chat.net.http.HttpRequest;
import rs.chat.net.http.HttpResponse;
import rs.chat.service.ChatService;
import rs.chat.service.UserService;

import java.io.IOException;

import static rs.chat.net.http.HttpResponse.HttpResponseBody;
import static rs.chat.router.Routes.GetRoute.ALL_CHATS_OF_USER_URL;
import static rs.chat.router.Routes.GetRoute.CHAT_INFO_URL;
import static rs.chat.router.Routes.PostRoute.CAN_USER_CONNECT_TO_CHAT_URL;
import static rs.chat.router.Routes.PostRoute.JOIN_CHAT_URL;

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

		response.ok().send("chats", this.chatService.getAllChatsOfUserGroupedByType(user.getId()));
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

	/**
	 * Allows a user to join a chat with a code that is provided by the chat owner.
	 *
	 * @param request  request object that contains the username of the user that wants to join the chat.
	 * @param response response object that contains the name of the chat to which the user has joined.
	 * @param code     code of the chat to which the user wants to join.
	 *
	 * @throws IOException if an error occurs while sending the response back to the client.
	 */
	@PostMapping(JOIN_CHAT_URL)
	public void joinChat(HttpRequest request, HttpResponse response, @PathVariable String code) throws IOException {
		if (code.isBlank()) {
			throw new BadRequestException("Chat code cannot be empty");
		}

		Chat chat = this.chatService.getChatByCode(code);

		Long userId = request.body().get("userId").getAsLong();

		if (this.chatService.userIsAlreadyInChat(userId, chat.getId())) {
			throw new BadRequestException("You are already in chat %s".formatted(chat.getName()));
		}

		this.chatService.addUserToChat(userId, chat.getId());

		response.status(HttpStatus.OK).send("name", chat.getName());
		// Update the user's chats list in frontend.
	}

	@PostMapping(CAN_USER_CONNECT_TO_CHAT_URL)
	public void canUserConnectToChat(HttpRequest request, HttpResponse response, @PathVariable Long chatId) throws IOException {
		Long userId = request.body().get("userId").getAsLong();

		response.ok().send("canConnect", this.chatService.userCanConnectToChat(userId, chatId));
	}
}
