package rs.chat.router;

public class Routes {
	private Routes() {
	}

	private static final String API_URL_PREFIX = "/api/v1";
	public static final String ROOT_URL = "/";

	public static final String REFRESH_TOKEN_URL = API_URL_PREFIX + "/token/refresh";
	public static final String WS_CHAT_ENDPOINT = "/ws/rschat";

	public static class GetRoute {
		public static final GetRoute INSTANCE = new GetRoute();

		private GetRoute() {
		}

		public static final String USERS_URL = API_URL_PREFIX + "/users";
		public static final String OPENED_SESSIONS_OF_USER_URL = API_URL_PREFIX + "/sessions/{username}";
		public static final String DEGREES_URL = API_URL_PREFIX + "/degrees";
		public static final String DEGREE_BY_NAME_URL = API_URL_PREFIX + "/degree/{degreeName}";

		public String[] lowTierRoutes() {
			return new String[] {
					OPENED_SESSIONS_OF_USER_URL, DEGREES_URL, DEGREE_BY_NAME_URL
			};
		}

		public String[] mediumTierRoutes() {
			return new String[] {

			};
		}

		public String[] topTierRoutes() {
			return new String[] {
					USERS_URL
			};
		}
	}

	public static class PostRoute {
		public static final PostRoute INSTANCE = new PostRoute();

		private PostRoute() {
		}

		public static final String LOGIN_URL = API_URL_PREFIX + "/login";
		public static final String LOGOUT_URL = API_URL_PREFIX + "/logout";
		public static final String REGISTER_URL = API_URL_PREFIX + "/register";

		public static final String USER_SAVE_URL = API_URL_PREFIX + "/user/save";
		public static final String DEGREE_SAVE_URL = API_URL_PREFIX + "/degree/save";

		public String[] lowTierRoutes() {
			return new String[] {

			};
		}

		public String[] mediumTierRoutes() {
			return new String[] {

			};
		}

		public String[] topTierRoutes() {
			return new String[] {
					USER_SAVE_URL, DEGREE_SAVE_URL
			};
		}
	}

	public static class PutRoute {
		public static final PutRoute INSTANCE = new PutRoute();

		private PutRoute() {
		}

		public static final String CHANGE_PASSWORD_URL = API_URL_PREFIX + "/changePassword/{username}";
		public static final String EDIT_DEGREE_NAME_URL = API_URL_PREFIX + "/degree/editName";

		public String[] lowTierRoutes() {
			return new String[] {
					CHANGE_PASSWORD_URL
			};
		}

		public String[] mediumTierRoutes() {
			return new String[] {

			};
		}

		public String[] topTierRoutes() {
			return new String[] {
					EDIT_DEGREE_NAME_URL
			};
		}
	}

	public static class DeleteRoute {
		public static final DeleteRoute INSTANCE = new DeleteRoute();

		private DeleteRoute() {
		}

		public static final String DELETE_DEGREE_URL = "/degree/delete/{degreeName}";

		public String[] lowTierRoutes() {
			return new String[] {

			};
		}

		public String[] mediumTierRoutes() {
			return new String[] {

			};
		}

		public String[] topTierRoutes() {
			return new String[] {
					DELETE_DEGREE_URL
			};
		}
	}
}
