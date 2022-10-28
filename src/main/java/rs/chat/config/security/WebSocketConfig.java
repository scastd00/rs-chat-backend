package rs.chat.config.security;

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
public class WebSocketConfig implements WebSocketConfigurer {
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
		registry.addHandler(new WebSocketHandler(), WS_CHAT_ENDPOINT)
		        .setAllowedOrigins(ACCEPTED_ORIGINS.toArray(STRING_ARRAY));
	}
}
