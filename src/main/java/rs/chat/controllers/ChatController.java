package rs.chat.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import rs.chat.domain.entity.Chat;
import rs.chat.domain.entity.User;
import rs.chat.domain.service.ChatService;
import rs.chat.domain.service.UserGroupService;
import rs.chat.domain.service.UserService;
import rs.chat.exceptions.BadRequestException;
import rs.chat.net.http.HttpRequest;
import rs.chat.net.http.HttpResponse;

import java.io.IOException;

import static org.springframework.http.HttpStatus.OK;
import static rs.chat.net.http.HttpResponse.HttpResponseBody;
import static rs.chat.router.Routes.GetRoute.ALL_CHATS_OF_USER_URL;
import static rs.chat.router.Routes.GetRoute.ALL_USERS_OF_CHAT_URL;
import static rs.chat.router.Routes.GetRoute.CHAT_INFO_URL;
import static rs.chat.router.Routes.PostRoute.CONNECT_TO_CHAT_URL;
import static rs.chat.router.Routes.PostRoute.JOIN_CHAT_URL;
import static rs.chat.router.Routes.PostRoute.LEAVE_CHAT_URL;
import static rs.chat.utils.Constants.GROUP;

/**
 * Controller that manages all chat-related requests.
 */
@Slf4j
@RestController
@RequiredArgsConstructor
public class ChatController {
	private final UserService userService;
	private final ChatService chatService;
	private final UserGroupService userGroupService;

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
		User user = ControllerUtils.performActionThatMayThrowException(response, () -> this.userService.getUser(username));

		response.ok().send("chats", this.chatService.getAllChatsOfUserGroupedByType(user.getId()));
	}

	/**
	 * Returns information about a chat.
	 *
	 * @param response response object that contains the name and metadata information
	 *                 of the chat.
	 * @param chatKey  key of the chat to be returned information about.
	 *
	 * @throws IOException if an error occurs while sending the response back to the client.
	 */
	@GetMapping(CHAT_INFO_URL)
	public void getChatInformation(HttpResponse response, @PathVariable String chatKey) throws IOException {
		Chat chat = ControllerUtils.performActionThatMayThrowException(response, () ->
				this.chatService.getChatByKey(chatKey)
				                .orElseThrow(() -> {
					                throw new BadRequestException("Chat with key=%s does not exist.".formatted(chatKey));
				                })
		);

		HttpResponseBody body = new HttpResponseBody("name", chat.getName());
		body.add("metadata", chat.getMetadata());

		response.ok().send(body);
	}

	/**
	 * Returns all users of a chat.
	 *
	 * @param response response object that contains the users of the chat.
	 * @param chatKey  key of the chat whose users are to be returned.
	 *
	 * @throws IOException if an error occurs while sending the response back to the client.
	 */
	@GetMapping(ALL_USERS_OF_CHAT_URL)
	public void getAllUsersOfChat(HttpResponse response, @PathVariable String chatKey) throws IOException {
		Chat chat = ControllerUtils.performActionThatMayThrowException(response, () ->
				this.chatService.getChatByKey(chatKey)
				                .orElseThrow(() -> {
					                throw new BadRequestException("Chat with key=%s does not exist.".formatted(chatKey));
				                })
		);

		response.ok().send("users", this.chatService.getAllUsersOfChat(chat.getId()));
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
		if (code.trim().isEmpty()) {
			response.badRequest().send("Chat code cannot be empty");
			log.warn("Chat code cannot be empty");
			return;
		}

		Chat chat = ControllerUtils.performActionThatMayThrowException(response, () -> this.chatService.getChatByCode(code));
		Long userId = request.body().get("userId").getAsLong();

		if (this.chatService.userAlreadyBelongsToChat(userId, chat.getId())) {
			response.badRequest().send("You are already in chat %s".formatted(chat.getName()));
			return;
		}

		this.chatService.addUserToChat(userId, chat.getId());

		// If the users join a group chat, add them to the group as well.
		// Users can only join degree and subject chats if they are added by teachers or admins.
		if (GROUP.equals(chat.getType())) {
			String key = chat.getKey().split("-")[1];
			this.userGroupService.addUserToGroup(userId, Long.parseLong(key));
		}

		response.ok().send("name", chat.getName());
		// Update the user's chats list in frontend.
	}

	@PostMapping(CONNECT_TO_CHAT_URL)
	public void connectToChat(HttpRequest request, HttpResponse response, @PathVariable String chatKey) throws IOException {
		Long userId = request.body().get("userId").getAsLong();

		response.ok().send("connect", this.chatService.connectToChat(userId, chatKey));
	}

	@PostMapping(LEAVE_CHAT_URL)
	public void leaveChat(HttpRequest request, HttpResponse response, @PathVariable String chatKey) throws IOException {
		Long userId = request.body().get("userId").getAsLong();

		this.chatService.removeUserFromChat(userId, chatKey);

		response.sendStatus(OK);
	}
}
