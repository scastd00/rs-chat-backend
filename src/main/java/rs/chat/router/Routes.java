package rs.chat.router;

public class Routes {
	private Routes() {
	}

	private static final String V_1 = "/api/v1";
	public static final String ROOT_URL = "/";

	public static final String REFRESH_TOKEN_URL = V_1 + "/token/refresh";
	public static final String WS_CHAT_ENDPOINT = "/ws/rschat";

	public static class GetRoute {
		public static final GetRoute INSTANCE = new GetRoute();

		private GetRoute() {
		}

		public static final String USERS_URL = V_1 + "/users";
		public static final String OPENED_SESSIONS_OF_USER_URL = V_1 + "/sessions/{username}";
		public static final String DEGREES_URL = V_1 + "/degrees";
		public static final String DEGREE_BY_NAME_URL = V_1 + "/degree/{degreeName}";
		public static final String SUBJECTS_URL = V_1 + "/subjects";
		public static final String GROUPS_URL = V_1 + "/groups";
		public static final String ALL_CHATS_OF_USER_URL = V_1 + "/chats/{username}";
		public static final String CHAT_INFO_URL = V_1 + "/chats/info/{id}";

		public String[] lowTierRoutes() {
			return new String[] {
					OPENED_SESSIONS_OF_USER_URL, DEGREES_URL, DEGREE_BY_NAME_URL,
					ALL_CHATS_OF_USER_URL, CHAT_INFO_URL
			};
		}

		public String[] mediumTierRoutes() {
			return new String[] {

			};
		}

		public String[] topTierRoutes() {
			return new String[] {
					USERS_URL, SUBJECTS_URL, GROUPS_URL
			};
		}
	}

	public static class PostRoute {
		public static final PostRoute INSTANCE = new PostRoute();

		private PostRoute() {
		}

		public static final String LOGIN_URL = V_1 + "/login";
		public static final String LOGOUT_URL = V_1 + "/logout";
		public static final String REGISTER_URL = V_1 + "/register";

		public static final String USER_SAVE_URL = V_1 + "/user/save";
		public static final String DEGREE_SAVE_URL = V_1 + "/degree/save";
		public static final String SUBJECT_SAVE_URL = V_1 + "/subject/save";
		public static final String GROUP_SAVE_URL = V_1 + "/group/save";

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
					USER_SAVE_URL, DEGREE_SAVE_URL, SUBJECT_SAVE_URL, GROUP_SAVE_URL
			};
		}
	}

	public static class PutRoute {
		public static final PutRoute INSTANCE = new PutRoute();

		private PutRoute() {
		}

		public static final String CHANGE_PASSWORD_URL = V_1 + "/changePassword/{username}";
		public static final String EDIT_DEGREE_NAME_URL = V_1 + "/degree/editName";

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
