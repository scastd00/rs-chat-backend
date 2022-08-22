package rs.chat.config.security;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import rs.chat.net.ws.WebSocketHandler;

import static rs.chat.router.Routes.WS_CHAT_ENDPOINT;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {
	@Override
	public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
		registry.addHandler(new WebSocketHandler(), WS_CHAT_ENDPOINT)
		        .setAllowedOrigins(
				        "https://rschat-ws.herokuapp.com/",
				        "http://localhost:3000"
		        );
	}
}
