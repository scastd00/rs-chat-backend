package rs.chat.storage;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import rs.chat.utils.Constants;
import rs.chat.utils.Utils;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.awscore.exception.AwsServiceException;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.core.sync.ResponseTransformer;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.HeadObjectRequest;
import software.amazon.awssdk.services.s3.model.HeadObjectResponse;
import software.amazon.awssdk.services.s3.model.ListObjectsRequest;
import software.amazon.awssdk.services.s3.model.ListObjectsResponse;
import software.amazon.awssdk.services.s3.model.NoSuchKeyException;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Object;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

import static rs.chat.net.ws.WebSocketMessageType.AUDIO_MESSAGE;
import static rs.chat.net.ws.WebSocketMessageType.IMAGE_MESSAGE;
import static rs.chat.net.ws.WebSocketMessageType.TEXT_MESSAGE;
import static rs.chat.net.ws.WebSocketMessageType.VIDEO_MESSAGE;
import static rs.chat.utils.Constants.CHAT_FILES_PATH;
import static rs.chat.utils.Constants.S3_BUCKET_NAME;

@Slf4j
public class S3 {
	private static final S3 INSTANCE = new S3();
	private final S3Client s3Client;

	private S3() {
		this.s3Client = S3Client.builder()
		                        .endpointOverride(Constants.S3_ENDPOINT_URI)
		                        .credentialsProvider(this::obtainCredentials)
		                        .region(Region.EU_WEST_3)
		                        .build();
	}

	public static S3 getInstance() {
		return INSTANCE;
	}

	public S3Client getS3Client() {
		return this.s3Client;
	}

	private AwsBasicCredentials obtainCredentials() {
		return AwsBasicCredentials.create(
				System.getenv("AWS_ACCESS_KEY_ID"),
				System.getenv("AWS_SECRET_ACCESS_KEY")
		);
	}

	public void logAllFilesInBucket() {
		ListObjectsRequest request = ListObjectsRequest.builder()
		                                               .bucket(S3_BUCKET_NAME)
		                                               .build();

		ListObjectsResponse response = this.s3Client.listObjects(request);
		List<S3Object> objects = response.contents();

		for (S3Object object : objects) {
			System.out.println("Key: " + object.key());
			System.out.println("Owner: " + object.owner());
			System.out.println("Size: " + object.size());
		}
	}

	public void uploadFile(String fileNameWithoutExtension, String messageType) {
		switch (messageType) {
			case TEXT_MESSAGE -> this.uploadFile(getFile("chats/", fileNameWithoutExtension, ".rsJson")); // Without opening and closing braces
			case IMAGE_MESSAGE -> this.uploadFile(getFile("images/", fileNameWithoutExtension, ".rsImg"));
			case AUDIO_MESSAGE -> this.uploadFile(getFile("audios/", fileNameWithoutExtension, ".rsAud"));
			case VIDEO_MESSAGE -> this.uploadFile(getFile("videos/", fileNameWithoutExtension, ".rsVid"));
			default -> throw new IllegalArgumentException("Unknown message type: " + messageType);
		}
	}

	@NotNull
	private File getFile(String x, String fileNameWithoutExtension, String x1) {
		return new File(CHAT_FILES_PATH + x + fileNameWithoutExtension + x1);
	}

	public void uploadFile(File file) {
		this.s3Client.putObject(
				PutObjectRequest.builder()
				                .bucket(S3_BUCKET_NAME)
				                .key(file.getName())
				                .build(),
				RequestBody.fromFile(file)
		);

		try {
			Files.delete(file.toPath());
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		this.logAllFilesInBucket();
	}

	@SneakyThrows
	public File downloadFile(String key) {
		File file = Utils.getChatFile(key);

		if (this.existsKeyInBucket(key + ".txt")) {
			this.s3Client.getObject(
					GetObjectRequest.builder()
					                .bucket(S3_BUCKET_NAME)
					                .key(key + ".txt")
					                .build(),
					ResponseTransformer.toFile(file)
			);
		}

		this.logAllFilesInBucket();
		return file;
	}

	private boolean existsKeyInBucket(String key) {
		HeadObjectResponse response;

		try {
			response = this.s3Client.headObject(
					HeadObjectRequest.builder()
					                 .bucket(S3_BUCKET_NAME)
					                 .key(key)
					                 .build()
			);
			log.info("");
		} catch (NoSuchKeyException ignored) {
			return false;
		} catch (AwsServiceException | SdkClientException e) {
			throw new RuntimeException(e);
		}

		return response.sdkHttpResponse().isSuccessful();
	}
}
