package ule.chat;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ULEChatApplication {
	public static void main(String[] args) {
		System.out.println("Server port: " + System.getenv("PORT"));
		SpringApplication.run(ULEChatApplication.class, args);
	}
}
