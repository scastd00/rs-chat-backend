package rs.chat.net.ws.strategies.commands;

import org.springframework.web.socket.TextMessage;
import rs.chat.exceptions.WebSocketException;
import rs.chat.net.ws.ChatManagement;

import java.io.IOException;
import java.util.Map;

import static rs.chat.net.ws.Message.COMMAND_RESPONSE;
import static rs.chat.utils.Utils.createMessage;

public class FlipCommandStrategy implements CommandStrategy {
	@Override
	public void handle(ChatManagement chatManagement, Map<String, Object> otherData)
			throws WebSocketException, IOException {
		getSession(otherData).sendMessage(new TextMessage(
				createMessage(
						"You flipped a coin and got: " + this.flip(),
						COMMAND_RESPONSE.type(),
						getClientID(otherData).chatId()
				)
		));
	}

	private String flip() {
		return Math.random() < 0.5 ? "Heads" : "Tails";
	}
}
