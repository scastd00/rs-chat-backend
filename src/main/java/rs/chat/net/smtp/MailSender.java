package rs.chat.net.smtp;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import rs.chat.tasks.TaskExecutionException;
import rs.chat.tasks.TaskScheduler;

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

import static rs.chat.tasks.Task.TaskStatus;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MailSender {
	private static final String FROM = System.getenv("GMAIL_ACCOUNT");
	private static final String SMTP_HOST = "smtp.gmail.com";
	private static final Session SESSION;

	static {
		// Setup mail server
		Properties properties = System.getProperties();
		properties.put("mail.smtp.host", SMTP_HOST);
		properties.put("mail.smtp.port", "465");
		properties.put("mail.smtp.auth", "true");
		properties.put("mail.smtp.ssl.enable", "true");
		properties.put("mail.smtp.socketFactory.port", "465");
		properties.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");

		// Get the Session object and pass username and password
		SESSION = Session.getInstance(properties, new Authenticator() {
			@Override
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(FROM, System.getenv("GMAIL_APP_PASSWORD"));
			}
		});

		// Used to debug SMTP issues
		SESSION.setDebug(false); // Prevent printing lots of debug info to console
	}

	public static void sendRegistrationEmailBackground(String to, String username) {
		TaskScheduler.executeTaskSecure(
				() -> {
					MimeMessage message = getMimeMessage(
							to,
							"Welcome to RSChat!",
							"src/main/resources/templates/email/welcome/rs-chat-welcome-email.html",
							"{{username}}",
							username
					);

					try {
						Transport.send(message);
					} catch (MessagingException e) {
						throw new TaskExecutionException(
								new TaskStatus(TaskStatus.FAILURE, e.getMessage())
						);
					}
				},
				exception -> {
					log.error("Failed to send registration email to {}", to, exception);
					return null;
				}
		);
	}

	public static void sendResetPasswordEmailBackground(String to, String code) {
		TaskScheduler.executeTaskSecure(
				() -> {
					MimeMessage message = getMimeMessage(
							to,
							"Reset your password",
							"src/main/resources/templates/email/resetPassword/rs-chat-reset-password-email.html",
							"{{code}}",
							code
					);
					try {
						Transport.send(message);
					} catch (MessagingException e) {
						throw new TaskExecutionException(
								new TaskStatus(TaskStatus.FAILURE, e.getMessage())
						);
					}
				},
				exception -> {
					log.error("Failed to send reset password email to {}", to, exception);
					return null;
				}
		);
	}

	private static MimeMessage getMimeMessage(String to, String subject, String fileName,
	                                          String target, String value) {
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
