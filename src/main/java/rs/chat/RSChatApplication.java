package rs.chat;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import rs.chat.storage.S3;

@SpringBootApplication
@Slf4j
public class RSChatApplication {
	public static void main(String[] args) {
		SpringApplication.run(RSChatApplication.class, args);

		S3.getInstance().checkS3BucketConnectivity();

		log.info("RSChatApplication started successfully at port {}", System.getenv("PORT"));

		Runtime.getRuntime().addShutdownHook(new Thread(() -> {
			log.info("Shutting down RSChatApplication");
			S3.getInstance().close();
		}));
	}
}
