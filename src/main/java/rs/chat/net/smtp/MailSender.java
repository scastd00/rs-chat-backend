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

	/**
	 * Sends an email to the specified recipient that has registered for an account.
	 *
	 * @param to       the recipient's email address.
	 * @param username the recipient's username.
	 */
	public static void sendRegistrationEmailBackground(String to, String username) {
		TaskScheduler.executeTaskSecure(
				() -> {
					try {
						Transport.send(getMimeMessage(
								to,
								"Welcome to RSChat!",
								"src/main/resources/templates/email/welcome/rs-chat-welcome-email.html",
								"{{username}}",
								username
						));
					} catch (MessagingException | IOException e) {
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

	/**
	 * Sends an email to the specified recipient that has requested a password reset.
	 *
	 * @param to   the recipient's email address.
	 * @param code the password reset code.
	 */
	public static void sendResetPasswordEmailBackground(String to, String code) {
		TaskScheduler.executeTaskSecure(
				() -> {
					try {
						Transport.send(getMimeMessage(
								to,
								"Reset your password",
								"src/main/resources/templates/email/resetPassword/rs-chat-reset-password-email.html",
								"{{code}}",
								code
						));
					} catch (MessagingException | IOException e) {
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

	/**
	 * Creates a MimeMessage object that can be used to send an email.
	 *
	 * @param to       the recipient's email address.
	 * @param subject  the subject of the email.
	 * @param fileName the name of the file that contains the email's content.
	 * @param target   the target string to replace in the email's content.
	 * @param value    the value to replace the target string with.
	 *
	 * @return a {@link MimeMessage} object that can be used to send an email.
	 *
	 * @throws MessagingException if an error occurs while creating the MimeMessage object.
	 * @throws IOException        if an error occurs while reading the email's content.
	 */
	private static MimeMessage getMimeMessage(String to, String subject, String fileName, String target,
	                                          String value) throws MessagingException, IOException {
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
	}
}
