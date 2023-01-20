package rs.chat.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.google.gson.Gson;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.net.URI;
import java.security.SecureRandom;
import java.time.Duration;
import java.util.List;
import java.util.Optional;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class Constants {
	public static final String CHAT_VERSION = "1.11.0";

	public static final List<String> ACCEPTED_ORIGINS = List.of(
			// Local development
			"http://localhost:3000",
			// Production deployments
			"https://rschat.vercel.app/",
			"https://rschat-scastd00.vercel.app/",
			"https://rschat-git-main-scastd00.vercel.app/",
			// Preview deployments
			"https://rschat-git-dev-scastd00.vercel.app/"
	);

	private static final String EMPTY_ENV_VAR = "None";

	public static final Gson GSON = new Gson();
	public static final String ERROR_JSON_KEY = "error";
	public static final String DATA_JSON_KEY = "data";
	public static final String CHAT_KEY_FORMAT = "%s-%s";
	public static final String SCHEDULE_STRING = "#SCHEDULE#";

	public static final String LOCAL_FILES_PATH = System.getProperty("user.home") + "/.appdata/rschat/data/";
	public static final String[] STRING_ARRAY = new String[0];
	public static final Duration TOKEN_EXPIRATION_DURATION_NORMAL = Duration.ofHours(4);
	public static final Duration TOKEN_EXPIRATION_DURATION_EXTENDED = Duration.ofDays(7);
	public static final Algorithm ALGORITHM = Algorithm.HMAC256(System.getenv("TOKEN_SECRET").getBytes());

	public static final String STUDENT_ROLE = "STUDENT";
	public static final String TEACHER_ROLE = "TEACHER";
	public static final String ADMIN_ROLE = "ADMINISTRATOR";
	public static final List<String> LOW_TIER_ROLES = List.of(STUDENT_ROLE, TEACHER_ROLE, ADMIN_ROLE);
	public static final List<String> MEDIUM_TIER_ROLES = List.of(TEACHER_ROLE, ADMIN_ROLE);
	public static final List<String> TOP_TIER_ROLES = List.of(ADMIN_ROLE);

	public static final JWTVerifier JWT_VERIFIER = JWT.require(ALGORITHM).build();
	public static final String JWT_TOKEN_PREFIX = "Bearer ";

	public static final String S3_BUCKET_NAME = System.getenv("AWS_S3_BUCKET_NAME");
	public static final URI REMOTE_S3_ENDPOINT_URI = Optional.of(System.getenv("S3_ENDPOINT_URI"))
	                                                         .filter(s -> !s.equals(EMPTY_ENV_VAR))
	                                                         .map(URI::create)
	                                                         .orElse(null);
	public static final URI S3_ENDPOINT_URI_FOR_FILES = URI.create(System.getenv("S3_ENDPOINT_URI_FOR_FILES"));

	public static final String USER = "user";
	public static final String USER_CHAT_S3_FOLDER_PREFIX = USER + "/";
	public static final String GROUP = "group";
	public static final String GROUP_CHAT_S3_FOLDER_PREFIX = GROUP + "/";
	public static final String SUBJECT = "subject";
	public static final String SUBJECT_CHAT_S3_FOLDER_PREFIX = SUBJECT + "/";
	public static final String DEGREE = "degree";
	public static final String DEGREE_CHAT_S3_FOLDER_PREFIX = DEGREE + "/";

	public static final int MAX_CHAT_HISTORY_PER_REQUEST = 65;
	public static final SecureRandom SECURE_RANDOM = new SecureRandom();
}
