package rs.chat.net.ws;

import lombok.extern.slf4j.Slf4j;

import javax.websocket.ClientEndpoint;
import javax.websocket.server.ServerEndpoint;

@ClientEndpoint
@ServerEndpoint("/ws/chat/")
@Slf4j
public class RSChatWebSocket {
//	private final CountDownLatch closureLatch = new CountDownLatch(1);
//	private final RSChatWSServer server = RSChatWSServer.getInstance();
//	private Session session;
//	private String username;
//
//	public Session getSession() {
//		return this.session;
//	}
//
//	public String getUsername() {
//		return this.username;
//	}
//
//	@OnWebSocketConnect
//	@API(status = EXPERIMENTAL)
//	public void onConnect(Session session) {
//		this.session = session;
//		this.server.addClient(this);
//		log.info("Socket connected: " + session);
//	}
//
//	@OnMessage
//	@API(status = EXPERIMENTAL)
//	public void onMessage(Session session, String message) {
//		log.info("Socket message: " + message);
//
//		// Todo: add more type of messages.
//	}
//
//	@OnClose
//	@API(status = EXPERIMENTAL)
//	public void onClose(CloseReason reason) {
//		closureLatch.countDown();
//	}
//
//	@OnError
//	@API(status = EXPERIMENTAL)
//	public void onError(Throwable cause) {
//		cause.printStackTrace(System.err);
//	}
//
//	@API(status = EXPERIMENTAL)
//	public void awaitClosure() throws InterruptedException {
//		log.debug("Awaiting closure from remote");
//		closureLatch.await();
//	}
//
//	@Override
//	public boolean equals(Object o) {
//		if (this == o) return true;
//		if (o == null || getClass() != o.getClass()) return false;
//		RSChatWebSocket other = (RSChatWebSocket) o;
//		return getSession().equals(other.getSession());
//	}
//
//	@Override
//	public int hashCode() {
//		return Objects.hash(getSession());
//	}
}
