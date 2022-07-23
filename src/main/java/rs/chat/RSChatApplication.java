package rs.chat;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import rs.chat.net.ws.RSChatWSServer;

import java.io.BufferedReader;
import java.io.InputStreamReader;

@SpringBootApplication
public class RSChatApplication {
	public static void main(String[] args) throws Exception {
//		SpringApplication.run(RSChatApplication.class, args);

//		S3.getInstance().logAllFilesInBucket();

		RSChatWSServer server = RSChatWSServer.getInstance();
		server.start();

		BufferedReader sysin = new BufferedReader(new InputStreamReader(System.in));
		while (true) {
			String in = sysin.readLine();
			server.broadcast(in);
		}
	}
}
