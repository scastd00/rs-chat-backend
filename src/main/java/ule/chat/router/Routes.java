package ule.chat.router;

import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.POST;

public final class Routes {
	private Routes() {
	}

	public static final String LOGIN_URL = "/api/login";
	public static final String LOGOUT_URL = "/api/logout";
	public static final String REGISTER_URL = "/api/register";
	public static final String UNREGISTER_URL = "/api/unregister";
	public static final String USER_URL = "/api/user";
	public static final String USERS_URL = "/api/users";
	public static final String USER_BY_ID_URL = "/api/user/{id}";
	public static final String USER_BY_USERNAME_URL = "/api/user/{username}";
	public static final String USER_BY_EMAIL_URL = "/api/user/{email}";
	public static final String USER_SAVE_URL = "/api/user/save";
	public static final String REFRESH_TOKEN_URL = "/api/token/refresh";

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
}
