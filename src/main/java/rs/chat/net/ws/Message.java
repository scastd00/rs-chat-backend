package rs.chat.net.ws;

import java.io.File;
import java.util.Objects;

import static rs.chat.utils.Constants.LOCAL_FILES_PATH;

/**
 * Record that specifies properties of the messages.
 * There are some default messages that the application uses.
 *
 * @param type       {@link String} that specifies the message type (defined in SCREAMING_SNAKE_CASE).
 * @param filePrefix path prefix of the files where the messages are stored in disk and S3.
 * @param extension  extension of the file created for a specific type of message.
 */
public record Message(String type, String filePrefix, String extension) {
	public static final Message USER_CONNECTED = new Message("USER_CONNECTED", null, null);
	public static final Message USER_DISCONNECTED = new Message("USER_DISCONNECTED", null, null);
	public static final Message USER_TYPING = new Message("USER_TYPING", null, null);
	public static final Message USER_STOPPED_TYPING = new Message("USER_STOPPED_TYPING", null, null);

	public static final Message USER_JOINED = new Message("USER_JOINED", null, null);
	public static final Message USER_LEFT = new Message("USER_LEFT", null, null);

	public static final Message TEXT_MESSAGE = new Message("TEXT_MESSAGE", "chat/", ".rsJson");
	public static final Message IMAGE_MESSAGE = new Message("IMAGE_MESSAGE", "images/", null);
	public static final Message AUDIO_MESSAGE = new Message("AUDIO_MESSAGE", "audios/", null);
	public static final Message VIDEO_MESSAGE = new Message("VIDEO_MESSAGE", "videos/", null);
	public static final Message PDF_MESSAGE = new Message("PDF_MESSAGE", "pdfs/", null);
	public static final Message TEXT_DOC_MESSAGE = new Message("TEXT_DOC_MESSAGE", "texts/", null);
	public static final Message COMMAND_MESSAGE = new Message("COMMAND_MESSAGE", null, null);
	public static final Message MENTION_MESSAGE = new Message("MENTION_MESSAGE", null, null);

	public static final Message ACTIVE_USERS_MESSAGE = new Message("ACTIVE_USERS", null, null);
	public static final Message GET_HISTORY_MESSAGE = new Message("GET_HISTORY", "chat/", TEXT_MESSAGE.extension);
	public static final Message INFO_MESSAGE = new Message("INFO_MESSAGE", null, null);
	public static final Message ERROR_MESSAGE = new Message("ERROR_MESSAGE", null, null);
	public static final Message PING_MESSAGE = new Message("PING", null, null);
	public static final Message PONG_MESSAGE = new Message("PONG", null, null);
	public static final Message RESTART_MESSAGE = new Message("RESTART", null, null);
	public static final Message MAINTENANCE_MESSAGE = new Message("MAINTENANCE", null, null);

	/**
	 * Returns the file that is used to store the message in disk.
	 *
	 * @param fileNameWithoutExtension name of the file without extension.
	 *
	 * @return {@link File} that is used to store the message in disk.
	 */
	public File buildFileInDisk(String fileNameWithoutExtension) {
		File file = this.historyFile(fileNameWithoutExtension);

		if (file.getParentFile() != null) {
			file.getParentFile().mkdirs(); // Create parent folder if it doesn't exist.
		}

		return file;
	}

	/**
	 * Returns the key that is used to store the file in S3 bucket.
	 *
	 * @param key name of the file without extension.
	 *
	 * @return key used to store the file in S3 bucket.
	 */
	public String s3Key(String key) {
		return this.filePrefix + key + this.extension;
	}

	/**
	 * Returns the file that is stored in disk to read from or write to the messages.
	 *
	 * @param fileNameWithoutExtension name of the file without extension.
	 *
	 * @return {@link File} that is stored in disk to read from or write to the messages.
	 */
	public File historyFile(String fileNameWithoutExtension) {
		return new File(LOCAL_FILES_PATH + this.s3Key(fileNameWithoutExtension));
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Message that = (Message) o;
		return type.equals(that.type);
	}

	@Override
	public int hashCode() {
		return Objects.hash(type, filePrefix, extension);
	}
}
