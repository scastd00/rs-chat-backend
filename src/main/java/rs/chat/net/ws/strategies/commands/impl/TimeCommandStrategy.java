package rs.chat.net.ws.strategies.commands.impl;

import org.springframework.web.socket.TextMessage;
import rs.chat.exceptions.WebSocketException;
import rs.chat.net.ws.strategies.commands.CommandHandlingDTO;
import rs.chat.net.ws.strategies.commands.CommandStrategy;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static rs.chat.net.ws.JsonMessageWrapper.createMessage;
import static rs.chat.net.ws.Message.COMMAND_RESPONSE;

public class TimeCommandStrategy implements CommandStrategy {
	@Override
	public void handle(CommandHandlingDTO handlingDTO) throws WebSocketException, IOException {
		handlingDTO.getSession().sendMessage(new TextMessage(
				createMessage(
						"Current time is: " +
								LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")),
						COMMAND_RESPONSE.type(),
						handlingDTO.getClientID().chatId()
				))
		);
	}

}
