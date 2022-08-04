package rs.chat;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import rs.chat.storage.S3;

@SpringBootApplication
public class RSChatApplication {
	public static void main(String[] args) {
		SpringApplication.run(RSChatApplication.class, args);

		S3.getInstance().checkS3BucketConnectivity();
	}
}
