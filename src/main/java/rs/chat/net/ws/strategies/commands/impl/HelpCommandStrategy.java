package rs.chat.net.ws.strategies.commands.impl;

import org.springframework.web.socket.TextMessage;
import rs.chat.exceptions.WebSocketException;
import rs.chat.net.ws.strategies.commands.CommandHandlingDTO;
import rs.chat.net.ws.strategies.commands.CommandMappings;
import rs.chat.net.ws.strategies.commands.CommandStrategy;

import java.io.IOException;

import static rs.chat.net.ws.Message.COMMAND_RESPONSE;
import static rs.chat.utils.Utils.createMessage;

public class HelpCommandStrategy implements CommandStrategy {
	@Override
	public void handle(CommandHandlingDTO handlingDTO) throws WebSocketException, IOException {
		getSession(handlingDTO.otherData()).sendMessage(new TextMessage(
				createMessage(
						"Available commands: " + CommandMappings.getAvailableCommandsWithDescriptionAndUsage(),
						COMMAND_RESPONSE.type(),
						getClientID(handlingDTO.otherData()).chatId()
				)
		));
	}

}
