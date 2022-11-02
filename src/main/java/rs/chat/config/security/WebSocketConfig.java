package rs.chat.config.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import rs.chat.net.ws.WebSocketHandler;

import static rs.chat.router.Routes.WS_CHAT_ENDPOINT;
import static rs.chat.utils.Constants.ACCEPTED_ORIGINS;
import static rs.chat.utils.Constants.STRING_ARRAY;

/**
 * Adds the handler that is used to handle web socket requests.
 */
@Configuration
@EnableWebSocket
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketConfigurer {
	private final WebSocketHandler webSocketHandler;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
		registry.addHandler(this.webSocketHandler, WS_CHAT_ENDPOINT)
		        .setAllowedOrigins(ACCEPTED_ORIGINS.toArray(STRING_ARRAY));
	}
}
