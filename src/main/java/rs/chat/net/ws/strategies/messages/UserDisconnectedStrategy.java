package rs.chat.net.ws.strategies.messages;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.socket.TextMessage;
import rs.chat.exceptions.WebSocketException;
import rs.chat.net.ws.ChatManagement;
import rs.chat.net.ws.JsonMessageWrapper;
import rs.chat.net.ws.Message;

import java.io.IOException;
import java.util.Map;

import static rs.chat.net.ws.Message.USER_DISCONNECTED;
import static rs.chat.utils.Utils.createMessage;

/**
 * Strategy for handling {@link Message#USER_DISCONNECTED} messages.
 */
@Slf4j
public class UserDisconnectedStrategy implements MessageStrategy {
	@Override
	public void handle(JsonMessageWrapper wrappedMessage, ChatManagement chatManagement,
	                   Map<String, Object> otherData) throws WebSocketException, IOException {
		getSession(otherData).sendMessage(new TextMessage(
				createMessage(
						"Disconnected from server",
						USER_DISCONNECTED.type(),
						""
				)
		));
	}
}
