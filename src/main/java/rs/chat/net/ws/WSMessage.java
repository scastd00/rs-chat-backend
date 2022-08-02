package rs.chat.net.ws;

import java.util.Objects;

public record WSMessage(String type, String filePrefix, String extension) {
	public static final WSMessage USER_CONNECTED = new WSMessage("USER_CONNECTED", null, null);
	public static final WSMessage USER_DISCONNECTED = new WSMessage("USER_DISCONNECTED", null, null);
	public static final WSMessage TEXT_MESSAGE = new WSMessage("TEXT_MESSAGE", "/tmp/chat/", ".rsJson");
	public static final WSMessage IMAGE_MESSAGE = new WSMessage("IMAGE_MESSAGE", "/tmp/images/", ".rsImg");
	public static final WSMessage AUDIO_MESSAGE = new WSMessage("AUDIO_MESSAGE", "/tmp/audios/", ".rsAud");
	public static final WSMessage VIDEO_MESSAGE = new WSMessage("VIDEO_MESSAGE", "/tmp/videos/", ".rsVid");
	public static final WSMessage ACTIVE_USERS_MESSAGE = new WSMessage("ACTIVE_USERS", null, null);
	public static final WSMessage SERVER_INFO_MESSAGE = new WSMessage("SERVER_INFO", null, null);
	public static final WSMessage ERROR_MESSAGE = new WSMessage("ERROR_MESSAGE", null, null);

	public String buildFile(String fileNameWithoutExtension) {
		return this.filePrefix + fileNameWithoutExtension + this.extension;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		WSMessage that = (WSMessage) o;
		return type.equals(that.type) &&
				Objects.equals(filePrefix, that.filePrefix) &&
				Objects.equals(extension, that.extension);
	}

	@Override
	public int hashCode() {
		return Objects.hash(type, filePrefix, extension);
	}
}
