package rs.chat.controllers;

import com.google.gson.JsonObject;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import rs.chat.domain.service.FriendService;
import rs.chat.net.http.HttpRequest;
import rs.chat.net.http.HttpResponse;

import java.io.IOException;

import static rs.chat.router.Routes.PostRoute.FRIEND_SWITCH_URL;

/**
 * Controller that manages all friend-related requests.
 */
@Slf4j
@RestController
@RequiredArgsConstructor
public class FriendController {
	private final FriendService friendService;

	/**
	 * Switches the state of a friend. If the friend is already added, it will be removed.
	 * If the friend is not added, it will be added.
	 *
	 * @param request  The request containing the username and friendUsername.
	 * @param response The response.
	 *
	 * @throws IOException If an error occurs while sending the response.
	 */
	@PostMapping(FRIEND_SWITCH_URL)
	public void switchFriendState(HttpRequest request, HttpServletResponse response) throws IOException {
		JsonObject body = request.body();
		String username = body.get("username").getAsString();
		String friendUsername = body.get("friendUsername").getAsString();

		boolean newState = this.friendService.switchFriendState(username, friendUsername);

		String message = newState ? "Friend added" : "Friend removed";
		new HttpResponse(response).ok().send(message);
	}
}
