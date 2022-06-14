package ule.chat.utils;

import com.auth0.jwt.algorithms.Algorithm;
import com.google.gson.Gson;

public final class Constants {
	public static final Gson GSON = new Gson();
	public static final int TOKEN_EXPIRATION_TIME = 1000 * 60 * 10;
	public static final int REFRESH_TOKEN_EXPIRATION_TIME = 1000 * 60 * 30; // 30 minutes
	public static final Algorithm ALGORITHM = Algorithm.HMAC256(System.getenv("TOKEN_SECRET").getBytes());

	public static final String STUDENT_ROLE = "ROLE_STUDENT";
	public static final String ADMIN_ROLE = "ROLE_ADMINISTRATOR";
	public static final String TEACHER_ROLE = "ROLE_TEACHER";
}
