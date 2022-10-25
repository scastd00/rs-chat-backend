package rs.chat.net.smtp;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;

import javax.mail.Authenticator;
import javax.mail.Message.RecipientType;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

@Slf4j
public class MailSender {
	private MailSender() {
	}

	private static final String FROM = System.getenv("GMAIL_ACCOUNT");
	private static final String SMTP_HOST = "smtp.gmail.com";
	private static final Properties PROPERTIES = System.getProperties();
	private static final Session SESSION;

	static {
		// Setup mail server
		PROPERTIES.put("mail.smtp.host", SMTP_HOST);
		PROPERTIES.put("mail.smtp.port", "465");
		PROPERTIES.put("mail.smtp.auth", "true");
		PROPERTIES.put("mail.smtp.ssl.enable", "true");
		PROPERTIES.put("mail.smtp.socketFactory.port", "465");
		PROPERTIES.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");

		// Get the Session object and pass username and password
		SESSION = Session.getInstance(PROPERTIES, new Authenticator() {
			@Override
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(FROM, System.getenv("GMAIL_APP_PASSWORD"));
			}
		});

		// Used to debug SMTP issues
		SESSION.setDebug(false); // Prevent printing lots of debug info to console
	}

	public static void sendRegistrationEmail(String to, String username) {
		MimeMessage message = getMimeMessage(to, "Welcome to RSChat!",
		                                     "src/main/resources/templates/email/welcome/rs-chat-welcome-email.html",
		                                     "{{username}}", username);
		try {
			Transport.send(message);
		} catch (MessagingException e) {
			throw new RuntimeException(e);
		}
	}

	public static void resetPassword(String to, String code) {
		MimeMessage message = getMimeMessage(to, "Reset your password",
		                                     "src/main/resources/templates/email/resetPassword/rs-chat-reset-password-email.html",
		                                     "{{code}}", code);
		try {
			Transport.send(message);
		} catch (MessagingException e) {
			throw new RuntimeException(e);
		}
	}

	private static MimeMessage getMimeMessage(String to, String subject, String fileName, String target, String value) {
		try {
			MimeMessage message = new MimeMessage(SESSION);

			message.setFrom(new InternetAddress(FROM));
			message.addRecipient(RecipientType.TO, new InternetAddress(to));
			message.setSubject(subject);
			message.setText(
					IOUtils.toString(new FileReader(fileName)).replace(target, value),
					StandardCharsets.UTF_8.name(),
					"html"
			);

			return message;
		} catch (MessagingException | IOException e) {
			throw new RuntimeException(e);
		}
	}
}
