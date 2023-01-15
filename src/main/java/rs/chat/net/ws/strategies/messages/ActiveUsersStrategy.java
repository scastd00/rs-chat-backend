package rs.chat.net.ws.strategies.messages;

import com.google.gson.JsonArray;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import rs.chat.exceptions.WebSocketException;
import rs.chat.net.ws.ChatManagement;
import rs.chat.net.ws.ClientID;
import rs.chat.net.ws.JsonMessageWrapper;
import rs.chat.net.ws.Message;
import rs.chat.utils.Utils;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import static rs.chat.net.ws.Message.ACTIVE_USERS_MESSAGE;

/**
 * Strategy for handling {@link Message#ACTIVE_USERS_MESSAGE} messages.
 */
@Slf4j
public class ActiveUsersStrategy implements MessageStrategy {
	@Override
	public void handle(JsonMessageWrapper wrappedMessage, ChatManagement chatManagement,
	                   Map<String, Object> otherData) throws WebSocketException, IOException {
		WebSocketSession session = getSession(otherData);
		ClientID clientID = (ClientID) otherData.get("clientID");

		List<String> usernamesOfChat = chatManagement.getUsernamesOfChat(clientID.chatId());

		// The sender username is removed from the list (because it is already connected).
		List<String> filteredUsers = usernamesOfChat.stream()
		                                            .filter(username -> !username.equals(clientID.username()))
		                                            .toList();
		session.sendMessage(
				new TextMessage(this.createActiveUsersMessage(filteredUsers))
		);
	}

	/**
	 * Creates a {@link String} message containing the active users given a
	 * {@link List<String>} of usernames.
	 *
	 * @param usernames the {@link List<String>} of usernames.
	 *
	 * @return the {@link String} message containing the active users.
	 */
	private String createActiveUsersMessage(List<String> usernames) {
		JsonArray usersArray = new JsonArray();
		usernames.forEach(usersArray::add);
		return Utils.createMessage(usersArray.toString(), ACTIVE_USERS_MESSAGE.type(), "");
		// In the client the chatId is ignored, so we minimize the size of the message with an empty string.
	}
}
