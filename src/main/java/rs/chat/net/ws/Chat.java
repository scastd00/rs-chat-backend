package rs.chat.net.ws;

import lombok.SneakyThrows;
import rs.chat.storage.MessageWriter;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class Chat {
	private final CopyOnWriteArrayList<WSClient> clients;
	private final MessageWriter writer;

	@SneakyThrows
	public Chat(String chatId) {
		this.clients = new CopyOnWriteArrayList<>();
		this.writer = new MessageWriter(chatId);
	}

	public List<WSClient> getClients() {
		return this.clients;
	}

	public MessageWriter getWriter() {
		return this.writer;
	}

	public void finish() {
		this.writer.close();
	}
}
