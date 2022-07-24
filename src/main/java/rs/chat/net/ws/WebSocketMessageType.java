package rs.chat.net.ws;

public final class WebSocketMessageType {
	public static final String NEW_USER = "NEW_USER";
	public static final String END_CONNECTION = "END_CONNECTION";
	public static final String USER_JOINED = "USER_JOINED";
	public static final String TEXT_MESSAGE = "TEXT_MESSAGE";
	public static final String IMAGE_MESSAGE = "IMAGE_MESSAGE";
	public static final String AUDIO_MESSAGE = "AUDIO_MESSAGE";
	public static final String VIDEO_MESSAGE = "VIDEO_MESSAGE";
	public static final String KEEP_ALIVE_MESSAGE = "KEEP_ALIVE";
	public static final String ACTIVE_USERS_MESSAGE = "ACTIVE_USERS";

	private WebSocketMessageType() {}
}
