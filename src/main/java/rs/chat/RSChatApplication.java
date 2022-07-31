package rs.chat;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class RSChatApplication {
	public static void main(String[] args) {
		SpringApplication.run(RSChatApplication.class, args);

//		S3.getInstance().logAllFilesInBucket();
	}
}
