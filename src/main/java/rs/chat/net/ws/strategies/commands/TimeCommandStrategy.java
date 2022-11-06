package rs.chat.net.ws.strategies.commands;

import org.springframework.web.socket.TextMessage;
import rs.chat.exceptions.WebSocketException;
import rs.chat.net.ws.WebSocketChatMap;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

import static rs.chat.net.ws.Message.COMMAND_RESPONSE;
import static rs.chat.utils.Utils.createMessage;

public class TimeCommandStrategy implements CommandStrategy {
	@Override
	public void handle(WebSocketChatMap webSocketChatMap, Map<String, Object> otherData)
			throws WebSocketException, IOException {
		getSession(otherData).sendMessage(new TextMessage(
				createMessage(
						"Current time is: " +
								LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")),
						COMMAND_RESPONSE.type(),
						getClientID(otherData).chatId()
				))
		);
	}
}
