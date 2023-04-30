package rs.chat.net.ws.strategies.commands.impl;

import com.google.gson.JsonArray;
import rs.chat.exceptions.WebSocketException;
import rs.chat.net.ws.ClientID;
import rs.chat.net.ws.strategies.commands.CommandHandlingDTO;
import rs.chat.net.ws.strategies.commands.CommandStrategy;

import java.io.IOException;

import static rs.chat.net.ws.Message.ACTIVE_USERS_MESSAGE;
import static rs.chat.net.ws.JsonMessageWrapper.createMessage;

public class BackCommandStrategy implements CommandStrategy {
	@Override
	public void handle(CommandHandlingDTO handlingDTO) throws WebSocketException, IOException {
		// Oposite of AwayCommandStrategy, send the list of users with this user
		ClientID clientID = handlingDTO.getClientID();
		String chatId = clientID.chatId();

		// Set the client as active
		handlingDTO.chatManagement().setClientActive(clientID);
		String usernameList = handlingDTO.chatManagement()
		                                 .getActiveUsernamesOfChat(chatId)
		                                 .stream()
		                                 .collect(JsonArray::new, JsonArray::add, JsonArray::addAll)
		                                 .toString();

		handlingDTO.chatManagement().broadcastToSingleChatWithoutSaving(
				chatId,
				createMessage(
						usernameList,
						ACTIVE_USERS_MESSAGE.type(),
						chatId
				)
		);
	}
}
