package rs.chat.strategies.message;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import rs.chat.exceptions.WebSocketException;
import rs.chat.net.ws.JsonMessageWrapper;
import rs.chat.net.ws.WSClientID;
import rs.chat.net.ws.WSMessage;
import rs.chat.net.ws.WebSocketChatMap;
import rs.chat.utils.Utils;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static rs.chat.net.ws.WSMessage.GET_HISTORY_MESSAGE;
import static rs.chat.net.ws.WSMessage.USER_JOINED;
import static rs.chat.net.ws.WSMessage.USER_LEFT;
import static rs.chat.utils.Constants.MAX_CHAT_HISTORY_PER_REQUEST;
import static rs.chat.utils.Utils.createServerMessage;

/**
 * Strategy for handling {@link WSMessage#GET_HISTORY_MESSAGE} messages.
 */
@Slf4j
public class GetHistoryStrategy implements MessageStrategy {
	@Override
	public void handle(JsonMessageWrapper wrappedMessage, WebSocketChatMap webSocketChatMap,
	                   Map<String, Object> otherData) throws WebSocketException, IOException {
		// Todo: receive a number of the "page" to get the history from.
		//  Received 1: get the last 65 messages.
		//  Received 2: get the 65 messages prior to the last 65 messages, and so on.
		/*
		 * Updated messages are retrieved from the file stored in disk instead of
		 * requesting them to the S3 bucket. The file is always present here because it is
		 * previously downloaded by S3 class.
		 */
		File historyFile = GET_HISTORY_MESSAGE.historyFile(wrappedMessage.chatId());
		String history = IOUtils.toString(new FileReader(historyFile));
		String username = ((WSClientID) otherData.get("wsClientID")).username();

		JsonArray lastMessages = new JsonArray();
		List<String> reversedHistory = Arrays.asList(history.split("\n"));
		Collections.reverse(reversedHistory); // Reverse the history to get the latest messages first.
		reversedHistory.stream()
		               .limit(MAX_CHAT_HISTORY_PER_REQUEST) // First, limit the number of messages to send, to improve performance.
		               .map(Utils::parseJson)
		               .filter(jsonObject -> this.filterUserActivityMessages(jsonObject, username))
		               .forEach(lastMessages::add);

		WebSocketSession session = (WebSocketSession) otherData.get("session");
		session.sendMessage(new TextMessage(
				createServerMessage(lastMessages.toString(), GET_HISTORY_MESSAGE.type(), wrappedMessage.chatId())
		));
	}

	/**
	 * Filter user activity messages of the current user from sending to the client.
	 *
	 * @param jsonObject {@link JsonObject} with all the messages in the history file,
	 *                   it will be modified by removing the activity
	 *                   messages of the current user.
	 * @param username   {@link String} with the current user's username.
	 *
	 * @return {@code true} if the message is not an activity message of the current user,
	 * {@code false} otherwise.
	 */
	private boolean filterUserActivityMessages(JsonObject jsonObject, String username) {
		JsonObject headers = (JsonObject) jsonObject.get("headers");
		String type = headers.get("type").getAsString();

		if (type.equals(USER_JOINED.type()) || type.equals(USER_LEFT.type())) {
			JsonObject body = (JsonObject) jsonObject.get("body");
			// Activity messages always have the username and a message.
			return !body.get("content").getAsString().contains(username);
		}

		return true;
	}
}
