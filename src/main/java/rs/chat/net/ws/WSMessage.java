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
public record WSMessage(String type, String filePrefix, String extension) {
	public static final WSMessage USER_JOINED = new WSMessage("USER_JOINED", null, null);
	public static final WSMessage USER_LEFT = new WSMessage("USER_LEFT", null, null);

	public static final WSMessage TEXT_MESSAGE = new WSMessage("TEXT_MESSAGE", "chat/", ".rsJson");
	public static final WSMessage IMAGE_MESSAGE = new WSMessage("IMAGE_MESSAGE", "images/", null);
	public static final WSMessage AUDIO_MESSAGE = new WSMessage("AUDIO_MESSAGE", "audios/", null);
	public static final WSMessage VIDEO_MESSAGE = new WSMessage("VIDEO_MESSAGE", "videos/", null);
	public static final WSMessage PDF_MESSAGE = new WSMessage("PDF_MESSAGE", "pdfs/", null);
	public static final WSMessage TEXT_DOC_MESSAGE = new WSMessage("TEXT_DOC_MESSAGE", "texts/", null);

	public static final WSMessage ACTIVE_USERS_MESSAGE = new WSMessage("ACTIVE_USERS", null, null);
	public static final WSMessage GET_HISTORY_MESSAGE = new WSMessage("GET_HISTORY", "chat/", TEXT_MESSAGE.extension);
	public static final WSMessage SERVER_INFO_MESSAGE = new WSMessage("SERVER_INFO", null, null);
	public static final WSMessage ERROR_MESSAGE = new WSMessage("ERROR_MESSAGE", null, null);
	public static final WSMessage PING_MESSAGE = new WSMessage("PING", null, null);
	public static final WSMessage PONG_MESSAGE = new WSMessage("PONG", null, null);
	public static final WSMessage RESTART_MESSAGE = new WSMessage("RESTART", null, null);

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
		WSMessage that = (WSMessage) o;
		return type.equals(that.type);
	}

	@Override
	public int hashCode() {
		return Objects.hash(type, filePrefix, extension);
	}
}
