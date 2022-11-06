package rs.chat.net.ws.strategies.messages;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import rs.chat.exceptions.WebSocketException;
import rs.chat.net.ws.ChatManagement;
import rs.chat.net.ws.ClientID;
import rs.chat.net.ws.JsonMessageWrapper;
import rs.chat.net.ws.Message;

import java.io.IOException;
import java.util.Map;

import static rs.chat.net.ws.Message.ERROR_MESSAGE;
import static rs.chat.utils.Utils.createMessage;

/**
 * Strategy for handling {@link Message#ERROR_MESSAGE} messages.
 */
@Slf4j
public class ErrorMessageStrategy implements MessageStrategy {
	@Override
	public void handle(JsonMessageWrapper wrappedMessage, ChatManagement chatManagement,
	                   Map<String, Object> otherData) throws WebSocketException, IOException {
		WebSocketSession session = (WebSocketSession) otherData.get("session");

		session.sendMessage(new TextMessage(
				createMessage(
						"ERROR: type property is not present in the content of the JSON",
						ERROR_MESSAGE.type(),
						((ClientID) otherData.get("clientID")).chatId()
				))
		);
	}
}
