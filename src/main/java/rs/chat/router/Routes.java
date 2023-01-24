package rs.chat.router;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Routes {
	private static final String V_1 = "/api/v1";
	public static final String ROOT_URL = "/";
	public static final String ALL_ROUTES = "/**";
	public static final String ACTUATOR_URL = "/actuator/prometheus";
	public static final String TEST_URL = V_1 + "/test";

	public static final String WS_CHAT_ENDPOINT = "/ws/rschat";

	/**
	 * GET routes for the application.
	 */
	@NoArgsConstructor(access = AccessLevel.PRIVATE)
	public static class GetRoute {
		public static final GetRoute INSTANCE = new GetRoute();

		public static final String USERS_URL = V_1 + "/users";
		public static final String OPENED_SESSIONS_OF_USER_URL = V_1 + "/sessions/{username}";
		public static final String DEGREES_URL = V_1 + "/degrees";
		public static final String DEGREE_BY_NAME_URL = V_1 + "/degree/{degreeName}";
		public static final String SUBJECTS_URL = V_1 + "/subjects";
		public static final String GROUPS_URL = V_1 + "/groups";
		public static final String ALL_CHATS_OF_USER_URL = V_1 + "/chats/{username}";
		public static final String CHAT_INFO_URL = V_1 + "/chats/info/{chatKey}";
		public static final String ALL_USERS_OF_CHAT_URL = V_1 + "/chat/users/{chatKey}";
		public static final String RANDOM_EMOJIS_URL = V_1 + "/emojis/random/{count}";
		public static final String EMOJI_STARTING_WITH_STRING_URL = V_1 + "/emojis/{string}";
		public static final String EMOJI_BY_CATEGORY_URL = V_1 + "/emojis/category/{category}";
		public static final String EMOJIS_GROUPED_BY_CATEGORY_URL = V_1 + "/emojis/grouped";
		public static final String USER_ID_BY_USERNAME_URL = V_1 + "/user/id/{username}";
		public static final String HEALTH_URL = V_1 + "/health";

		public static final String TEACHER_DEGREES_URL = V_1 + "/teacher/degrees/{id}";
		public static final String TEACHER_SUBJECTS_URL = V_1 + "/teacher/subjects/{id}";
		public static final String TEACHERS_URL = V_1 + "/teachers";

		/**
		 * Generate an array containing all GET routes allowed to low tier users.
		 *
		 * @return an array containing all GET routes allowed to low tier users.
		 */
		public String[] lowTierRoutes() {
			return new String[] {
					OPENED_SESSIONS_OF_USER_URL, DEGREES_URL, DEGREE_BY_NAME_URL,
					ALL_CHATS_OF_USER_URL, CHAT_INFO_URL, ALL_USERS_OF_CHAT_URL, RANDOM_EMOJIS_URL,
					EMOJI_STARTING_WITH_STRING_URL, EMOJI_BY_CATEGORY_URL, EMOJIS_GROUPED_BY_CATEGORY_URL,
					USER_ID_BY_USERNAME_URL
			};
		}

		/**
		 * Generate an array containing all GET routes allowed to medium tier users.
		 *
		 * @return an array containing all GET routes allowed to medium tier users.
		 */
		public String[] mediumTierRoutes() {
			return new String[] {
					TEACHER_DEGREES_URL, TEACHER_SUBJECTS_URL
			};
		}

		/**
		 * Generate an array containing all GET routes allowed to top tier users.
		 *
		 * @return an array containing all GET routes allowed to top tier users.
		 */
		public String[] topTierRoutes() {
			return new String[] {
					USERS_URL, SUBJECTS_URL, GROUPS_URL, TEACHERS_URL
			};
		}
	}

	/**
	 * POST routes for the application.
	 */
	@NoArgsConstructor(access = AccessLevel.PRIVATE)
	public static class PostRoute {
		public static final PostRoute INSTANCE = new PostRoute();

		public static final String LOGIN_URL = V_1 + "/login";
		public static final String LOGOUT_URL = V_1 + "/logout";
		public static final String REGISTER_URL = V_1 + "/register";
		public static final String FORGOT_PASSWORD_URL = V_1 + "/forgotPassword";
		public static final String CREATE_PASSWORD_URL = V_1 + "/createPassword";

		public static final String USER_SAVE_URL = V_1 + "/user/save";
		public static final String DEGREE_SAVE_URL = V_1 + "/degree/save";
		public static final String SUBJECT_SAVE_URL = V_1 + "/subject/save";
		public static final String GROUP_SAVE_URL = V_1 + "/group/save";
		public static final String DELETE_USER_URL = V_1 + "/user/delete/{id}";

		public static final String UPLOAD_URL = V_1 + "/upload";

		public static final String JOIN_CHAT_URL = V_1 + "/chat/join/{code}";
		public static final String CONNECT_TO_CHAT_URL = V_1 + "/chat/connect/{chatKey}";
		public static final String LEAVE_CHAT_URL = V_1 + "/chat/leave/{chatKey}";

		public static final String ADD_TEACHER_TO_SUBJECT_URL = V_1 + "/teacher/subject/add";

		/**
		 * Generate an array containing all POST routes allowed to low tier users.
		 *
		 * @return an array containing all POST routes allowed to low tier users.
		 */
		public String[] lowTierRoutes() {
			return new String[] {
					UPLOAD_URL, JOIN_CHAT_URL, CONNECT_TO_CHAT_URL, LEAVE_CHAT_URL
			};
		}

		/**
		 * Generate an array containing all POST routes allowed to medium tier users.
		 *
		 * @return an array containing all POST routes allowed to medium tier users.
		 */
		public String[] mediumTierRoutes() {
			return new String[] {

			};
		}

		/**
		 * Generate an array containing all POST routes allowed to top tier users.
		 *
		 * @return an array containing all POST routes allowed to top tier users.
		 */
		public String[] topTierRoutes() {
			return new String[] {
					USER_SAVE_URL, DEGREE_SAVE_URL, SUBJECT_SAVE_URL, GROUP_SAVE_URL,
					ADD_TEACHER_TO_SUBJECT_URL, DELETE_USER_URL
			};
		}
	}

	/**
	 * PUT routes for the application.
	 */
	@NoArgsConstructor(access = AccessLevel.PRIVATE)
	public static class PutRoute {
		public static final PutRoute INSTANCE = new PutRoute();

		public static final String EDIT_DEGREE_NAME_URL = V_1 + "/degree/editName";

		/**
		 * Generate an array containing all PUT routes allowed to low tier users.
		 *
		 * @return an array containing all PUT routes allowed to low tier users.
		 */
		public String[] lowTierRoutes() {
			return new String[] {

			};
		}

		/**
		 * Generate an array containing all PUT routes allowed to medium tier users.
		 *
		 * @return an array containing all PUT routes allowed to medium tier users.
		 */
		public String[] mediumTierRoutes() {
			return new String[] {

			};
		}

		/**
		 * Generate an array containing all PUT routes allowed to top tier users.
		 *
		 * @return an array containing all PUT routes allowed to top tier users.
		 */
		public String[] topTierRoutes() {
			return new String[] {
					EDIT_DEGREE_NAME_URL
			};
		}
	}

	/**
	 * DELETE routes for the application.
	 */
	@NoArgsConstructor(access = AccessLevel.PRIVATE)
	public static class DeleteRoute {
		public static final DeleteRoute INSTANCE = new DeleteRoute();

		public static final String DELETE_DEGREE_URL = V_1 + "/degree/delete/{id}";
		public static final String DELETE_SUBJECT_URL = V_1 + "/subject/delete/{id}";
		public static final String DELETE_GROUP_URL = V_1 + "/group/delete/{id}";

		/**
		 * Generate an array containing all DELETE routes allowed to low tier users.
		 *
		 * @return an array containing all DELETE routes allowed to low tier users.
		 */
		public String[] lowTierRoutes() {
			return new String[] {

			};
		}

		/**
		 * Generate an array containing all DELETE routes allowed to medium tier users.
		 *
		 * @return an array containing all DELETE routes allowed to medium tier users.
		 */
		public String[] mediumTierRoutes() {
			return new String[] {

			};
		}

		/**
		 * Generate an array containing all DELETE routes allowed to top tier users.
		 *
		 * @return an array containing all DELETE routes allowed to top tier users.
		 */
		public String[] topTierRoutes() {
			return new String[] {
					DELETE_DEGREE_URL, DELETE_SUBJECT_URL, DELETE_GROUP_URL
			};
		}
	}
}
