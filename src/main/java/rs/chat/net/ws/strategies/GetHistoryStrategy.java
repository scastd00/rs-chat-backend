package rs.chat.net.ws.strategies;

import com.google.gson.JsonArray;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import rs.chat.exceptions.WebSocketException;
import rs.chat.net.ws.JsonMessageWrapper;
import rs.chat.net.ws.WebSocketChatMap;
import rs.chat.storage.S3;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static rs.chat.net.ws.WSMessage.GET_HISTORY_MESSAGE;
import static rs.chat.utils.Constants.MAX_CHAT_HISTORY_PER_REQUEST;
import static rs.chat.utils.Utils.createServerMessage;

@Slf4j
public class GetHistoryStrategy implements MessageStrategy {
	private final S3 s3 = S3.getInstance();

	@Override
	public void handle(JsonMessageWrapper wrappedMessage, WebSocketChatMap webSocketChatMap,
	                   Map<String, Object> otherData) throws WebSocketException, IOException {
		String history = this.s3.getContentsOfFile(
				wrappedMessage.chatId(), GET_HISTORY_MESSAGE
		);

		if (history == null) {
			log.warn("Could not get history for chat {}. Chat did not exist", wrappedMessage.chatId());
			return;
		}

		JsonArray jsonArray = new JsonArray();
		List<String> reversedHistory = Arrays.asList(history.split("\n"));
		Collections.reverse(reversedHistory); // Reverse the history to get the latest messages first.
		reversedHistory.stream()
		               .limit(MAX_CHAT_HISTORY_PER_REQUEST)
		               .forEach(jsonArray::add);

		WebSocketSession session = (WebSocketSession) otherData.get("session");
		session.sendMessage(new TextMessage(
				createServerMessage(jsonArray.toString(), GET_HISTORY_MESSAGE.type(), wrappedMessage.chatId())
		));
	}
}
