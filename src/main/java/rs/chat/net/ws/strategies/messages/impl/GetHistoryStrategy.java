package rs.chat.net.ws.strategies.messages.impl;

import com.google.gson.JsonArray;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.socket.TextMessage;
import rs.chat.json.JsonParser;
import rs.chat.mem.cache.CachedHistoryFile;
import rs.chat.mem.cache.HistoryFilesCache;
import rs.chat.exceptions.WebSocketException;
import rs.chat.net.ws.Message;
import rs.chat.net.ws.strategies.messages.MessageHandlingDTO;
import rs.chat.net.ws.strategies.messages.MessageStrategy;

import java.io.IOException;

import static rs.chat.net.ws.Message.GET_HISTORY_MESSAGE;
import static rs.chat.net.ws.JsonMessageWrapper.createMessage;

/**
 * Strategy for handling {@link Message#GET_HISTORY_MESSAGE} messages.
 */
@Slf4j
public class GetHistoryStrategy implements MessageStrategy {
	@Override
	public void handle(MessageHandlingDTO handlingDTO) throws WebSocketException, IOException {
		/*
		 * Messages are read from the history file and sent to the client. This file is cached
		 * in memory to speed up the process of reading the messages. The cache is updated
		 * every time a new message is sent to the chat. The client must send the page number
		 * of the history file that it wants to read. The default page size is {@link Constants#HISTORY_PAGE_SIZE}.
		 */

		// The offset of the messages that the client has already received
		int numberOfReceivedMessagesByClient = Integer.parseInt(handlingDTO.wrappedMessage().content());
		CachedHistoryFile historyFile = HistoryFilesCache.INSTANCE.get(handlingDTO.wrappedMessage().chatId());

		JsonArray lastMessages = historyFile.getMoreMessagesFromOffset(numberOfReceivedMessagesByClient)
		                                    .stream()
		                                    .map(JsonParser::parseJson)
		                                    .collect(JsonArray::new, JsonArray::add, JsonArray::addAll);

		handlingDTO.getSession().sendMessage(new TextMessage(
				createMessage(lastMessages.toString(), GET_HISTORY_MESSAGE.type(), handlingDTO.wrappedMessage().chatId())
		));
	}
}
