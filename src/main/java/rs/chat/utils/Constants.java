package rs.chat.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.google.gson.Gson;

import java.net.URI;

public final class Constants {
	private Constants() {
	}

	public static final Gson GSON = new Gson();
	public static final String ERROR_JSON_KEY = "error";

	public static final int TOKEN_EXPIRATION_TIME = 1000 * 60 * 60 * 4; // 4 hours
	public static final int REFRESH_TOKEN_EXPIRATION_TIME = 1000 * 60 * 60 * 24 * 15; // 15 days
	public static final Algorithm ALGORITHM = Algorithm.HMAC256(System.getenv("TOKEN_SECRET").getBytes());

	public static final String STUDENT_ROLE = "STUDENT";
	public static final String ADMIN_ROLE = "ADMINISTRATOR";
	public static final String TEACHER_ROLE = "TEACHER";

	public static final JWTVerifier JWT_VERIFIER = JWT.require(ALGORITHM).build();
	public static final String JWT_TOKEN_PREFIX = "Bearer ";

	public static final String S3_BUCKET_NAME = System.getenv("AWS_S3_BUCKET_NAME");
	public static final URI S3_ENDPOINT_URI = URI.create("http://localhost:4566");
}
