package rs.chat.net.ws;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.websocket.javax.server.JavaxWebSocketServletContainerInitializer;

import java.net.InetSocketAddress;

@Slf4j
public class WSServer {
	private static final WSServer INSTANCE = new WSServer(Integer.parseInt(System.getenv("WS_PORT")));
	private final Server server;

	// Todo: refactor this to include a chatMap in each endpoint (to have clients
	//  associated in each endpoint).
	private final WebSocketChatMap chatMap = new WebSocketChatMap();

	private WSServer(int port) {
		this.server = new Server(new InetSocketAddress(port));
		ServerConnector connector = new ServerConnector(this.server);
		this.server.addConnector(connector);

		// Set up the basic application "context" for this application at "/"
		// This is also known as the handler tree (in jetty speak)
		ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
		context.setContextPath("/");
		this.server.setHandler(context);

		// Initialize javax.websocket layer
		JavaxWebSocketServletContainerInitializer.configure(context, (servletContext, wsContainer) -> {
			// Configure defaults for container
			wsContainer.setDefaultMaxTextMessageBufferSize(65535);
			wsContainer.setDefaultMaxSessionIdleTimeout(-1); // Infinite timeout

			// Add WebSocket endpoint to javax.websocket layer
			wsContainer.addEndpoint(WSEndpoint.class);
		});

		connector.setReuseAddress(true);

		// When closing the VM, stop WS server
		Runtime.getRuntime().addShutdownHook(new Thread(this::stop));
	}

	public static WSServer getInstance() {
		return INSTANCE;
	}

	public WebSocketChatMap getChatMap() {
		return this.chatMap;
	}

	@SneakyThrows(Exception.class)
	public void start() {
		this.server.start();
		log.debug("Server started at {}", this.server.getURI());
	}

	@SneakyThrows(Exception.class)
	public void stop() {
		this.server.stop();
		log.debug("Server stopped");
	}

	public void join() throws InterruptedException {
		this.server.join();
	}
}
