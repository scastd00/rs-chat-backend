package rs.chat.mail;

import org.apache.commons.io.IOUtils;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMailMessage;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

import static rs.chat.utils.Constants.APPLICATION_EMAIL;

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
		MimeMessage message = getMimeMessage(to, "Welcome to RSChat!",
		                                     "src/main/resources/templates/email/welcome/rs-chat-welcome-email.html",
		                                     "{{username}}", username);
		sender.send(message);
	}

	public static void resetPassword(String to, String code) {
		MimeMessage message = getMimeMessage(to, "Reset your password",
		                                     "src/main/resources/templates/email/resetPassword/rs-chat-reset-password-email.html",
		                                     "{{code}}", code);
		sender.send(message);
	}

	private static MimeMessage getMimeMessage(String to, String subject, String fileName, String target, String value) {
		MimeMailMessage message = new MimeMailMessage(sender.createMimeMessage());

		try {
			message.setFrom(APPLICATION_EMAIL);
			message.setTo(to);
			message.setSubject(subject);
			message.getMimeMessage().setText(
					IOUtils.toString(new FileReader(fileName)).replace(target, value),
					StandardCharsets.UTF_8.name(),
					"html"
			);
		} catch (MessagingException | IOException e) {
			throw new RuntimeException(e);
		}

		return message.getMimeMessage();
	}
}
