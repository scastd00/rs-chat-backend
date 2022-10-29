package rs.chat.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.google.gson.Gson;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.io.File;
import java.net.URI;
import java.time.Duration;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class Constants {
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

	public static final Gson GSON = new Gson();
	public static final String ERROR_JSON_KEY = "error";

	public static final String LOCAL_FILES_PATH = "/tmp" + File.separator;
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
	public static final URI LOCAL_S3_ENDPOINT_URI = URI.create("http://localhost:4566/");
	public static final URI REMOTE_S3_ENDPOINT_URI = null;
	public static final URI LOCAL_S3_ENDPOINT_URI_FOR_FILES = URI.create("https://rs-chat-local.s3.localhost.localstack.cloud:4566/");
	public static final URI REMOTE_S3_ENDPOINT_URI_FOR_FILES = URI.create("https://rs-chat-bucket.s3.eu-west-3.amazonaws.com/");

	public static final String USER_CHAT = "user";
	public static final String USER_CHAT_S3_FOLDER_PREFIX = USER_CHAT + "/";

	public static final String GROUP_CHAT = "group";
	public static final String GROUP_CHAT_S3_FOLDER_PREFIX = GROUP_CHAT + "/";

	public static final String SUBJECT_CHAT = "subject";
	public static final String SUBJECT_CHAT_S3_FOLDER_PREFIX = SUBJECT_CHAT + "/";

	public static final String DEGREE_CHAT = "degree";
	public static final String DEGREE_CHAT_S3_FOLDER_PREFIX = DEGREE_CHAT + "/";

	public static final int MAX_CHAT_HISTORY_PER_REQUEST = 65;
}
