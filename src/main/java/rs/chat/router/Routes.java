package rs.chat.router;

import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.POST;

public final class Routes {
	private Routes() {
	}

	private static final String API_URL_PREFIX = "/api/v1";

	public static final String ROOT_URL = "/";
	public static final String LOGIN_URL = API_URL_PREFIX + "/login";
	public static final String LOGOUT_URL = API_URL_PREFIX + "/logout";
	public static final String REGISTER_URL = API_URL_PREFIX + "/register";
	public static final String UNREGISTER_URL = API_URL_PREFIX + "/unregister";
	public static final String USER_URL = API_URL_PREFIX + "/user";
	public static final String USERS_URL = API_URL_PREFIX + "/users";
	public static final String USER_BY_ID_URL = API_URL_PREFIX + "/user/{id}";
	public static final String USER_BY_USERNAME_URL = API_URL_PREFIX + "/user/{username}";
	public static final String USER_BY_EMAIL_URL = API_URL_PREFIX + "/user/{email}";
	public static final String USER_SAVE_URL = API_URL_PREFIX + "/user/save";
	public static final String REFRESH_TOKEN_URL = API_URL_PREFIX + "/token/refresh";
	public static final String OPENED_SESSIONS_URL = API_URL_PREFIX + "/sessions/{username}";

	public static final Route ROOT = new Route(GET, ROOT_URL);
	public static final Route LOGIN = new Route(GET, LOGIN_URL);
	public static final Route LOGOUT = new Route(GET, LOGOUT_URL);
	public static final Route REGISTER = new Route(POST, REGISTER_URL);
	public static final Route UNREGISTER = new Route(POST, UNREGISTER_URL);
	public static final Route USER = new Route(GET, USER_URL);
	public static final Route USERS = new Route(GET, USERS_URL);
	public static final Route USER_BY_ID = new Route(GET, USER_BY_ID_URL);
	public static final Route USER_BY_USERNAME = new Route(GET, USER_BY_USERNAME_URL);
	public static final Route USER_BY_EMAIL = new Route(GET, USER_BY_EMAIL_URL);
	public static final Route USER_SAVE = new Route(POST, USER_SAVE_URL);
	public static final Route REFRESH_TOKEN = new Route(POST, REFRESH_TOKEN_URL);
	public static final Route OPENED_SESSIONS = new Route(GET, OPENED_SESSIONS_URL);
}
