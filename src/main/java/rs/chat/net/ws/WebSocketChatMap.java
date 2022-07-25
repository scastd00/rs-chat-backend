package rs.chat.net.ws;

import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

@NoArgsConstructor
public class WebSocketChatMap {
	private final Map<String, CopyOnWriteArrayList<RSChatWebSocketClient>> chats = new HashMap<>();

	private synchronized void createChat(String chatId) {
		this.chats.put(chatId, new CopyOnWriteArrayList<>());
	}

	public RSChatWebSocketClient getClientByUsernameAndDate(String chatId, String username, long dateSignIn) {
		RSChatWebSocketClient clientFound = null;

		for (RSChatWebSocketClient client : this.chats.get(chatId)) {
			if (client.getUsername().equals(username)) {
				clientFound = client;
				break;
			}
		}

		return clientFound;
	}

	public synchronized void addClientToChat(RSChatWebSocketClient client) {
		String chatId = client.getChatId();

		if (!this.chats.containsKey(chatId)) {
			this.createChat(chatId);
		}

		this.chats.get(chatId).add(client);
	}

	public synchronized void removeClientFromChat(RSChatWebSocketClient client) {
		String chatId = client.getChatId();

		this.chats.get(chatId).remove(client);

		if (this.chats.get(chatId).isEmpty()) {
			this.chats.remove(chatId); // Delete the list of clients since there are no more clients in that chat.
		}
	}

	public void broadcastToSingleChat(String chatId, String message) {
		for (RSChatWebSocketClient client : this.chats.get(chatId)) {
			client.send(message);
		}
	}

	public void broadcastToSingleChatAndExcludeClient(String chatId,
	                                                  String message,
	                                                  RSChatWebSocketClient client) {
		for (RSChatWebSocketClient socketClient : this.chats.get(chatId)) {
			if (!socketClient.equals(client)) {
				socketClient.send(message);
			}
		}
	}

	public void totalBroadcast(String message) {
		for (CopyOnWriteArrayList<RSChatWebSocketClient> chatList : chats.values()) {
			for (RSChatWebSocketClient client : chatList) {
				client.send(message);
			}
		}
	}
}
