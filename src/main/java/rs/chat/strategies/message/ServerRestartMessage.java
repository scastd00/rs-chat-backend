package rs.chat.strategies.message;

import rs.chat.exceptions.WebSocketException;
import rs.chat.net.ws.JsonMessageWrapper;
import rs.chat.net.ws.WSMessage;
import rs.chat.net.ws.WebSocketChatMap;
import rs.chat.tasks.DefaultTasks;
import rs.chat.utils.Utils;

import java.io.IOException;
import java.util.Map;

import static rs.chat.net.ws.WSMessage.TEXT_MESSAGE;
import static rs.chat.utils.Utils.createServerMessage;

/**
 * Strategy for handling {@link WSMessage#RESTART_MESSAGE} messages.
 */
// Todo: check if we can convert all messages to @Component and remove WebSocketChatMap from the parameters.
public class ServerRestartMessage implements MessageStrategy {
	@Override
	public void handle(JsonMessageWrapper wrappedMessage, WebSocketChatMap webSocketChatMap,
	                   Map<String, Object> otherData) throws WebSocketException, IOException {
		webSocketChatMap.broadcastToSingleChat(
				wrappedMessage.chatId(),
				createServerMessage("Server is restarting in 5 minutes.", TEXT_MESSAGE.type(), wrappedMessage.chatId())
		);

		Utils.taskExecutor().submit(DefaultTasks.SHUTDOWN.getTask());
	}
}
