package rs.chat;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import rs.chat.net.ws.WSServer;

@SpringBootApplication
public class RSChatApplication {
	public static void main(String[] args) throws Exception {
		SpringApplication.run(RSChatApplication.class, args);

//		S3.getInstance().logAllFilesInBucket();

		WSServer server = WSServer.getInstance();
		server.start();
		server.join();
	}
}
