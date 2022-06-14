package ule.chat.router;

import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.POST;

public final class Routes {
	private Routes() {
	}

	public static final Route<String> LOGIN = new Route<>(GET, "/api/login");
	public static final Route<String> LOGOUT = new Route<>(GET, "/logout");
	public static final Route<String> REGISTER = new Route<>(POST, "/api/register");
	public static final Route<String> USER = new Route<>(GET, "/api/user");
	public static final Route<String> USERS = new Route<>(GET, "/api/users");
	public static final Route<String> USER_BY_ID = new Route<>(GET, "/api/user/{id}");
	public static final Route<String> USER_BY_USERNAME = new Route<>(GET, "/api/user/{username}");
	public static final Route<String> USER_BY_EMAIL = new Route<>(GET, "/api/user/{email}");
	public static final Route<String> USER_SAVE = new Route<>(POST, "/api/user/save");
	public static final Route<String> REFRESH_TOKEN = new Route<>(POST, "/api/token/refresh");
}
