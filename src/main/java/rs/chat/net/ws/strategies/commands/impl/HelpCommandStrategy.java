package rs.chat.net.ws.strategies.commands.impl;

import org.springframework.web.socket.TextMessage;
import rs.chat.exceptions.WebSocketException;
import rs.chat.net.ws.strategies.commands.CommandHandlingDTO;
import rs.chat.net.ws.strategies.commands.CommandMappings;
import rs.chat.net.ws.strategies.commands.CommandStrategy;

import java.io.IOException;

import static rs.chat.net.ws.Message.COMMAND_RESPONSE;
import static rs.chat.net.ws.JsonMessageWrapper.createMessage;

public class HelpCommandStrategy implements CommandStrategy {
	@Override
	public void handle(CommandHandlingDTO handlingDTO) throws WebSocketException, IOException {
		handlingDTO.getSession().sendMessage(new TextMessage(
				createMessage(
						"Available commands: " + CommandMappings.getAvailableCommandsWithDescriptionAndUsage(),
						COMMAND_RESPONSE.type(),
						handlingDTO.getClientID().chatId()
				)
		));
	}

}
