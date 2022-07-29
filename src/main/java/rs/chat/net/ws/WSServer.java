package rs.chat.net.ws;

import lombok.extern.slf4j.Slf4j;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.websocket.javax.server.JavaxWebSocketServletContainerInitializer;

import javax.websocket.ClientEndpoint;
import javax.websocket.server.ServerEndpoint;

@Slf4j
@ClientEndpoint
@ServerEndpoint(value = "/chat/")
public class WSServer {
	private static final WSServer INSTANCE = new WSServer();
	private final Server server = new Server();
	private final ServerConnector connector = new ServerConnector(this.server);
	// Todo: refactor this to include a chatMap in each endpoint (to have clients
	//  associated in each endpoint).
	private final WebSocketChatMap chatMap = new WebSocketChatMap();

	public WSServer() {
		this.server.addConnector(this.connector);

		// Set up the basic application "context" for this application at "/"
		// This is also known as the handler tree (in jetty speak)
		ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
		context.setContextPath("/");
		this.server.setHandler(context);

		// Initialize javax.websocket layer
		JavaxWebSocketServletContainerInitializer.configure(context, (servletContext, wsContainer) -> {
			// Configure defaults for container
			wsContainer.setDefaultMaxTextMessageBufferSize(65535);
			wsContainer.setDefaultMaxSessionIdleTimeout(-1); // Infinite timeout TODO: ¿clients are disconnected?

			// Add WebSocket endpoint to javax.websocket layer
			wsContainer.addEndpoint(WSEndpoint.class);
		});

//		this.connector.setReuseAddress(true);
//		this.connector.setIdleTimeout(-1); // Permanently connected sockets
	}

	public static WSServer getInstance() {
		return INSTANCE;
	}

	public WebSocketChatMap getChatMap() {
		return this.chatMap;
	}

	public void setPort(int port) {
		this.connector.setPort(port);
	}

	public void start() throws Exception {
		this.server.start();
	}

	public void stop() throws Exception {
		this.server.stop();
	}

	public void join() throws InterruptedException {
		this.server.join();
	}
}
