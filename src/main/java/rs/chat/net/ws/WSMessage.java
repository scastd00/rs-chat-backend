package rs.chat.net.ws;

import java.io.File;
import java.util.Objects;

import static rs.chat.utils.Constants.LOCAL_FILES_PATH;

public record WSMessage(String type, String filePrefix, String extension) {
	public static final WSMessage USER_CONNECTED = new WSMessage("USER_CONNECTED", null, null);
	public static final WSMessage USER_DISCONNECTED = new WSMessage("USER_DISCONNECTED", null, null);
	public static final WSMessage TEXT_MESSAGE = new WSMessage("TEXT_MESSAGE", "chat/", ".rsJson");
	public static final WSMessage IMAGE_MESSAGE = new WSMessage("IMAGE_MESSAGE", "images/", ".rsImg");
	public static final WSMessage AUDIO_MESSAGE = new WSMessage("AUDIO_MESSAGE", "audios/", ".rsAud");
	public static final WSMessage VIDEO_MESSAGE = new WSMessage("VIDEO_MESSAGE", "videos/", ".rsVid");
	public static final WSMessage ACTIVE_USERS_MESSAGE = new WSMessage("ACTIVE_USERS", null, null);
	public static final WSMessage SERVER_INFO_MESSAGE = new WSMessage("SERVER_INFO", null, null);
	public static final WSMessage ERROR_MESSAGE = new WSMessage("ERROR_MESSAGE", null, null);

	public File buildFileInDisk(String fileNameWithoutExtension) {
		File file = new File(LOCAL_FILES_PATH + this.s3Key(fileNameWithoutExtension));

		if (file.getParentFile() != null) {
			file.getParentFile().mkdirs();
		}

		return file;
	}

	public String s3Key(String key) {
		return this.filePrefix + key + this.extension;
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
