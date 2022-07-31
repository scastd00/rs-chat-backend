package rs.chat.net.ws;

public final class WebSocketMessageType {
	public static final String USER_CONNECTED = "USER_CONNECTED";
	public static final String USER_DISCONNECTED = "USER_DISCONNECTED";
	public static final String TEXT_MESSAGE = "TEXT_MESSAGE";
	public static final String IMAGE_MESSAGE = "IMAGE_MESSAGE";
	public static final String AUDIO_MESSAGE = "AUDIO_MESSAGE";
	public static final String VIDEO_MESSAGE = "VIDEO_MESSAGE";
	public static final String ACTIVE_USERS_MESSAGE = "ACTIVE_USERS";
	public static final String SERVER_INFO_MESSAGE = "SERVER_INFO";
	public static final String ERROR_MESSAGE = "ERROR_MESSAGE";

	private WebSocketMessageType() {
	}
}
