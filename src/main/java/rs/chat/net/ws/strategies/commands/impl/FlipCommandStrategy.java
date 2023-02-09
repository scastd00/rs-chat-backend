package rs.chat.net.ws.strategies.commands.impl;

import org.springframework.web.socket.TextMessage;
import rs.chat.exceptions.WebSocketException;
import rs.chat.net.ws.strategies.commands.CommandHandlingDTO;
import rs.chat.net.ws.strategies.commands.CommandStrategy;

import java.io.IOException;

import static rs.chat.net.ws.Message.COMMAND_RESPONSE;
import static rs.chat.utils.Utils.createMessage;

public class FlipCommandStrategy implements CommandStrategy {
	@Override
	public void handle(CommandHandlingDTO handlingDTO) throws WebSocketException, IOException {
		getSession(handlingDTO.otherData()).sendMessage(new TextMessage(
				createMessage(
						"You flipped a coin and got: " + this.flip(),
						COMMAND_RESPONSE.type(),
						getClientID(handlingDTO.otherData()).chatId()
				)
		));
	}

	private String flip() {
		return Math.random() < 0.5 ? "Heads" : "Tails";
	}
}
