package rs.chat.router;

public class Routes {
	private Routes() {
	}

	private static final String API_URL_PREFIX = "/api/v1";
	public static final String ROOT_URL = "/";
	public static final String REFRESH_TOKEN_URL = API_URL_PREFIX + "/token/refresh";
	public static final String WS_CHAT_ENDPOINT = "/ws/rschat";

	public static class GetRoute {
		private GetRoute() {
		}

		public static final String USER_URL = API_URL_PREFIX + "/user";
		public static final String USERS_URL = API_URL_PREFIX + "/users";
		public static final String OPENED_SESSIONS_OF_USER_URL = API_URL_PREFIX + "/sessions/{username}";
		public static final String CHAT_METADATA_URL = API_URL_PREFIX + "/chat/metadata/{type}/{id}";
		public static final String CHAT_CONTENT_URL = API_URL_PREFIX + "/chat/content/{type}/{id}";
		public static final String DEGREES_URL = API_URL_PREFIX + "/degrees";
		public static final String DEGREE_SAVE_URL = API_URL_PREFIX + "/degree/save";
		public static final String DEGREE_BY_NAME_URL = API_URL_PREFIX + "/degree/{degreeName}";
	}

	public static class PostRoute {
		private PostRoute() {
		}

		public static final String LOGIN_URL = API_URL_PREFIX + "/login";
		public static final String LOGOUT_URL = API_URL_PREFIX + "/logout";
		public static final String REGISTER_URL = API_URL_PREFIX + "/register";
		public static final String USER_SAVE_URL = API_URL_PREFIX + "/user/save";
		public static final String CHAT_SEND_TEXT_MESSAGE_URL = API_URL_PREFIX + "/chat/send/{chatId}";
	}

	public static class PutRoute {
		private PutRoute() {
		}

		public static final String CHANGE_PASSWORD_URL = API_URL_PREFIX + "/changePassword/{username}";
		public static final String EDIT_DEGREE_NAME_URL = API_URL_PREFIX + "/degree/editName";
	}

	public static class DeleteRoute {
		private DeleteRoute() {
		}

		public static final String DELETE_DEGREE_URL = "/degree/delete/{degreeName}";
	}
}
