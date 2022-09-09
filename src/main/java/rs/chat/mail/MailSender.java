package rs.chat.mail;

import org.apache.commons.io.IOUtils;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMailMessage;

import javax.mail.MessagingException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

public class MailSender {
	private static final JavaMailSender sender;

	private MailSender() {
	}

	static {
		JavaMailSenderImpl mailSender = new JavaMailSenderImpl();

		mailSender.setHost("smtp-mail.outlook.com");
		mailSender.setPort(587);
		mailSender.setUsername(System.getenv("EMAIL_USERNAME"));
		mailSender.setPassword(System.getenv("EMAIL_PASSWORD"));

		Properties props = mailSender.getJavaMailProperties();
		props.put("mail.transport.protocol", "smtp");
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.starttls.enable", "true");
//		props.put("mail.debug", "true");

		sender = mailSender;
	}

	public static void sendRegistrationEmail(String to, String username) {
		MimeMailMessage message = new MimeMailMessage(sender.createMimeMessage());

		try {
			message.setFrom("rschatws@outlook.com");
			message.setTo(to);
			message.setSubject("Welcome to RSChat!");
			message.getMimeMessage().setText(
					IOUtils.toString(new FileReader("src/main/resources/templates/email/welcome/rs-chat-welcome-email.html"))
					       .replace("{{username}}", username),
					StandardCharsets.UTF_8.name(),
					"html"
			);
		} catch (MessagingException | IOException e) {
			throw new RuntimeException(e);
		}

		sender.send(message.getMimeMessage());
	}

	public static void changePassword(String to, String username, String token) {
		MimeMailMessage message = new MimeMailMessage(sender.createMimeMessage());

		try {
			message.setFrom("rschatws@outlook.com");
			message.setTo(to);
			message.setSubject("Reset your password");
			message.getMimeMessage().setText(
					IOUtils.toString(new FileReader("src/main/resources/templates/email/resetPassword/rs-chat-reset-password-email.html"))
					       .replace("{{username}}", username)
					       .replace("{{token}}", token),
					StandardCharsets.UTF_8.name(),
					"html"
			);
		} catch (MessagingException | IOException e) {
			throw new RuntimeException(e);
		}

		sender.send(message.getMimeMessage());
	}
}
