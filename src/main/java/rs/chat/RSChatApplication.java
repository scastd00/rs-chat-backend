package rs.chat;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import rs.chat.net.ws.RSChatWebSocketServer;

@SpringBootApplication
public class RSChatApplication {
	public static void main(String[] args) throws Exception {
//		SpringApplication.run(RSChatApplication.class, args);

//		S3.getInstance().logAllFilesInBucket();

		RSChatWebSocketServer server = RSChatWebSocketServer.getInstance();
		server.start();
	}
}
