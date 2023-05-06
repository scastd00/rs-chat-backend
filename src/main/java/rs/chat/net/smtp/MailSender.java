package rs.chat.net.smtp;

import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import rs.chat.tasks.Task;
import rs.chat.tasks.TaskExecutionException;
import rs.chat.tasks.TaskScheduler;

import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Properties;
import java.util.function.Function;

import static rs.chat.tasks.Task.TaskStatus;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MailSender {
	private static final String FROM = System.getenv("GMAIL_ACCOUNT");
	private static final String SMTP_HOST = "smtp.gmail.com";
	private static final JavaMailSenderImpl SENDER = new JavaMailSenderImpl();

	static {
		SENDER.setHost(SMTP_HOST);
		SENDER.setPort(587);

		SENDER.setUsername(FROM);
		SENDER.setPassword(System.getenv("GMAIL_APP_PASSWORD"));

		Properties props = SENDER.getJavaMailProperties();
		props.setProperty("mail.transport.protocol", "smtp");
		props.setProperty("mail.smtp.auth", "true");
		props.setProperty("mail.smtp.ssl.enable", "true");
		props.setProperty("mail.smtp.starttls.enable", "true");
		props.setProperty("mail.smtp.socketFactory.port", "465");
		props.setProperty("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
		props.setProperty("mail.debug", "false");
	}

	/**
	 * Sends an email to the specified recipient that has registered for an account.
	 *
	 * @param to       the recipient's email address.
	 * @param username the recipient's username.
	 */
	public static void sendRegistrationEmailBackground(String to, String username) {
		TaskScheduler.executeTaskSecure(
				createSendTask(
						to,
						"Welcome to RSChat!",
						"src/main/resources/templates/email/welcome/rs-chat-welcome-email.html",
						Map.of("{{username}}", username)
				),
				exceptionHandler("Failed to send registration email to {}", to)
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
				createSendTask(
						to,
						"Reset your password",
						"src/main/resources/templates/email/resetPassword/rs-chat-reset-password-email.html",
						Map.of("{{code}}", code)
				),
				exceptionHandler("Failed to send reset password email to {}", to)
		);
	}

	/**
	 * Sends an email to the specified recipient that has been invited to join a chat.
	 *
	 * @param from     user that sent the invitation.
	 * @param to       user that received the invitation.
	 * @param chatName name of the chat.
	 * @param code     invitation code.
	 */
	public static void sendInvitationEmailBackground(String from, String to, String chatName, String code) {
		TaskScheduler.executeTaskSecure(
				createSendTask(
						to,
						"Invitation to join %s".formatted(chatName),
						"src/main/resources/templates/email/invite/rs-chat-invite-email.html",
						Map.of("{{code}}", code, "{{from}}", from, "{{chatName}}", chatName)
				),
				exceptionHandler("Failed to send invite code email to {}", to)
		);
	}

	/**
	 * Creates a MimeMessage object that can be used to send an email.
	 *
	 * @param to                the recipient's email address.
	 * @param subject           the subject of the email.
	 * @param fileName          the name of the file that contains the email's content.
	 * @param targetReplacement the target string to replace (with the value) in the email's content.
	 *
	 * @return a {@link MimeMessage} object that can be used to send an email.
	 *
	 * @throws MessagingException if an error occurs while creating the MimeMessage object.
	 * @throws IOException        if an error occurs while reading the email's content.
	 */
	private static MimeMessage getMimeMessage(String to, String subject, String fileName,
	                                          Map<String, String> targetReplacement) throws MessagingException, IOException {
		MimeMessage message = SENDER.createMimeMessage();
		message.setFrom(FROM);
		message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
		message.setSubject(subject);

		String content = IOUtils.toString(new FileReader(fileName));

		for (var entry : targetReplacement.entrySet()) {
			content = content.replace(entry.getKey(), entry.getValue());
		}

		message.setText(content, StandardCharsets.UTF_8.name(), "html");
		return message;
	}

	/**
	 * Generates a task that sends an email to the specified recipient.
	 *
	 * @param to           email address of the recipient.
	 * @param subject      subject of the email.
	 * @param fileName     name of the file that contains the email's content.
	 * @param replacements target string to replace (with the value) in the email's content.
	 *
	 * @return a task that sends an email to the specified recipient.
	 */
	@NotNull
	private static Task createSendTask(String to, String subject, String fileName, Map<String, String> replacements) {
		return () -> {
			try {
				SENDER.send(getMimeMessage(
						to,
						subject,
						fileName,
						replacements
				));
			} catch (MessagingException | IOException e) {
				throw new TaskExecutionException(
						new TaskStatus(TaskStatus.FAILURE, e.getMessage())
				);
			}
		};
	}

	/**
	 * Creates a new task exception handler with the specified format and format variables.
	 *
	 * @param format     the format of the exception message.
	 * @param formatVars the format variables.
	 *
	 * @return an exception handler with the specified format and format variables.
	 */
	@NotNull
	private static Function<TaskExecutionException, Void> exceptionHandler(String format, String... formatVars) {
		return exception -> {
			log.error(format, formatVars, exception);
			return null;
		};
	}
}
