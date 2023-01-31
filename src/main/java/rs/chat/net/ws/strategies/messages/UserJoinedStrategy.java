package rs.chat.net.ws.strategies.messages;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.socket.handler.ConcurrentWebSocketSessionDecorator;
import rs.chat.exceptions.WebSocketException;
import rs.chat.net.ws.ChatManagement;
import rs.chat.net.ws.Client;
import rs.chat.net.ws.ClientID;
import rs.chat.net.ws.JsonMessageWrapper;
import rs.chat.net.ws.Message;

import java.io.IOException;
import java.util.Map;

import static rs.chat.net.ws.Message.USER_JOINED;
import static rs.chat.utils.Constants.MAX_FILE_BYTES;
import static rs.chat.utils.Utils.createMessage;

/**
 * Strategy for handling {@link Message#USER_JOINED} messages.
 */
@Slf4j
public class UserJoinedStrategy implements MessageStrategy {
	@Override
	public void handle(JsonMessageWrapper wrappedMessage, ChatManagement chatManagement,
	                   Map<String, Object> otherData) throws WebSocketException, IOException {
		ClientID clientID = (ClientID) otherData.get("clientID");
		String chatId = clientID.chatId();
		String username = clientID.username();

		ConcurrentWebSocketSessionDecorator decorator = new ConcurrentWebSocketSessionDecorator(
				getSession(otherData),
				Integer.MAX_VALUE,
				Math.toIntExact(MAX_FILE_BYTES)
		);

		chatManagement.addClientToChat(new Client(decorator, clientID));
		chatManagement.broadcastToSingleChatAndExcludeClient(
				createMessage(username + " has joined the chat", USER_JOINED.type(), chatId),
				clientID
		);

		log.debug(username + " has joined the chat");
	}
}
