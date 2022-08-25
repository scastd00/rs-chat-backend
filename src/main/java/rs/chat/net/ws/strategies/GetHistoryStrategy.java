package rs.chat.net.ws.strategies;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import rs.chat.exceptions.WebSocketException;
import rs.chat.net.ws.JsonMessageWrapper;
import rs.chat.net.ws.WSClientID;
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

@Slf4j
public class GetHistoryStrategy implements MessageStrategy {
	@Override
	public void handle(JsonMessageWrapper wrappedMessage, WebSocketChatMap webSocketChatMap,
	                   Map<String, Object> otherData) throws WebSocketException, IOException {
		/*
		 * Chat history file is always present in disk when this method is executed.
		 * Get the updated messages from the file in disk.
		 */
		File historyFile = GET_HISTORY_MESSAGE.historyFile(wrappedMessage.chatId());
		String history = IOUtils.toString(new FileReader(historyFile));
		String username = ((WSClientID) otherData.get("wsClientID")).username();

		JsonArray jsonArray = new JsonArray();
		List<String> reversedHistory = Arrays.asList(history.split("\n"));
		Collections.reverse(reversedHistory); // Reverse the history to get the latest messages first.
		reversedHistory.stream()
		               .limit(MAX_CHAT_HISTORY_PER_REQUEST)
		               .map(Utils::parseJson)
		               .filter(jsonObject -> this.filterUserActivityMessages(jsonObject, username))
		               .forEach(jsonArray::add);

		WebSocketSession session = (WebSocketSession) otherData.get("session");
		session.sendMessage(new TextMessage(
				createServerMessage(jsonArray.toString(), GET_HISTORY_MESSAGE.type(), wrappedMessage.chatId())
		));
	}

	// Hide connection messages of the current user from sending to the client.
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
