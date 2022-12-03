package rs.chat.storage;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.jetbrains.annotations.Nullable;
import rs.chat.utils.Utils;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.core.exception.SdkException;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.core.sync.ResponseTransformer;
import software.amazon.awssdk.http.SdkHttpResponse;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.HeadBucketRequest;
import software.amazon.awssdk.services.s3.model.HeadBucketResponse;
import software.amazon.awssdk.services.s3.model.HeadObjectRequest;
import software.amazon.awssdk.services.s3.model.HeadObjectResponse;
import software.amazon.awssdk.services.s3.model.NoSuchKeyException;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.time.LocalDate;
import java.util.Map;

import static rs.chat.net.ws.Message.TEXT_MESSAGE;
import static rs.chat.utils.Constants.DOCKER_S3_ENDPOINT_URI;
import static rs.chat.utils.Constants.LOCAL_S3_ENDPOINT_URI;
import static rs.chat.utils.Constants.REMOTE_S3_ENDPOINT_URI;
import static rs.chat.utils.Constants.S3_BUCKET_NAME;
import static rs.chat.utils.Utils.isDevEnv;
import static rs.chat.utils.Utils.isDockerEnv;

/**
 * Class that provides utility methods to work with S3.
 */
@Slf4j
public final class S3 implements Closeable {
	private static final S3 INSTANCE = new S3();
	private final S3Client s3Client;

	/**
	 * Private constructor that initializes the S3 client.
	 */
	private S3() {
		this.s3Client = S3Client.builder()
		                        .endpointOverride(getEndpointOverride())
		                        .credentialsProvider(this::obtainCredentials)
		                        .region(Region.EU_WEST_3)
		                        .build();
	}

	@Nullable
	private static URI getEndpointOverride() {
		if (isDockerEnv()) {
			return DOCKER_S3_ENDPOINT_URI;
		}

		if (isDevEnv()) {
			return LOCAL_S3_ENDPOINT_URI;
		}

		return REMOTE_S3_ENDPOINT_URI;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void close() {
		this.s3Client.close();
	}

	/**
	 * @return the singleton instance of the S3 class
	 */
	public static S3 getInstance() {
		return INSTANCE;
	}

	/**
	 * @return the credentials for the S3 client
	 */
	private AwsBasicCredentials obtainCredentials() {
		return AwsBasicCredentials.create(
				System.getenv("AWS_ACCESS_KEY_ID"),
				System.getenv("AWS_SECRET_ACCESS_KEY")
		);
	}

	/**
	 * Checks if the S3 bucket exists.
	 *
	 * @throws SdkException if the bucket does not exist or there is an error.
	 */
	public void checkS3BucketConnectivity() {
		HeadBucketResponse headBucketResponse;

		try {
			headBucketResponse = this.s3Client.headBucket(
					HeadBucketRequest.builder()
					                 .bucket(S3_BUCKET_NAME)
					                 .build()
			);
		} catch (S3Exception e) {
			this.s3Client.createBucket(b -> b.bucket(S3_BUCKET_NAME));
			return;
		}

		SdkHttpResponse response = headBucketResponse.sdkHttpResponse();

		if (response.isSuccessful()) {
			log.debug("Successful HeadBucketResponse. Status code: {}", response.statusCode());
		} else {
			log.error("HeadBucketResponse failed. Status code: {}", response.statusCode());
			throw SdkException.create("HeadBucketResponse failed. Status code: " + response.statusCode(), null);
		}
	}

	/**
	 * Uploads a chat history file to S3 bucket.
	 *
	 * @param chatId the chat id of the chat history file to upload to S3 bucket.
	 */
	public void uploadHistoryFile(String chatId) {
		File file = TEXT_MESSAGE.buildFileInDisk(chatId);
		String s3Key = TEXT_MESSAGE.s3Key(chatId);

		this.s3Client.putObject(
				PutObjectRequest.builder()
				                .bucket(S3_BUCKET_NAME)
				                .key(s3Key)
				                .build(),
				RequestBody.fromFile(file)
		);

		try {
			// Delete the file from the local disk after it has been uploaded to S3.
			Files.delete(file.toPath());
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		log.debug("Uploaded file {} to S3 bucket with key {} and deleted from disk", file.getName(), s3Key);
	}

	/**
	 * Downloads a chat history file from S3 bucket.
	 *
	 * @param chatId the chat id of the chat history file to download from S3 bucket.
	 *
	 * @return the downloaded chat history file.
	 */
	public File downloadHistoryFile(String chatId) {
		File file = TEXT_MESSAGE.buildFileInDisk(chatId);
		String s3Key = TEXT_MESSAGE.s3Key(chatId);

		if (this.existsKeyInBucket(s3Key)) {
			this.s3Client.getObject(
					GetObjectRequest.builder()
					                .bucket(S3_BUCKET_NAME)
					                .key(s3Key)
					                .build(),
					ResponseTransformer.toFile(file)
			);

			log.debug("Downloaded file {} from S3 bucket with key {} and saved to disk", file.getName(), s3Key);
		}

		return file;
	}

	/**
	 * Deletes a chat history file from S3 bucket.
	 *
	 * @param chatId the chat id of the chat history file to delete from S3 bucket.
	 */
	public void deleteHistoryFile(String chatId) {
		// Todo (IMPORTANT): The chat must be empty before deleting the history file.
		//  If there are users inside, not known the side effects of deleting the history file.
		String s3Key = TEXT_MESSAGE.s3Key(chatId);

		if (this.existsKeyInBucket(s3Key)) {
			this.s3Client.deleteObject(
					DeleteObjectRequest.builder()
					                   .bucket(S3_BUCKET_NAME)
					                   .key(s3Key)
					                   .build()
			);

			log.debug("Deleted file from S3 bucket with key {}", s3Key);
		}
	}

	/**
	 * Uploads a file (as byte[]) to S3 bucket and returns the URL of the file.
	 *
	 * @param mediaType the media type of the file to upload to S3 bucket.
	 * @param fileName  the name of the file to upload to S3 bucket.
	 * @param dataBytes bytes of the file.
	 * @param metadata  metadata of the file.
	 *
	 * @return the URI of the file in S3 bucket.
	 */
	public URI uploadFile(String mediaType, String fileName, byte[] dataBytes, Map<String, String> metadata) {
		String s3Key = this.s3Key(mediaType, fileName);

		this.s3Client.putObject(
				PutObjectRequest.builder()
				                .bucket(S3_BUCKET_NAME)
				                .key(s3Key)
				                .metadata(metadata)
				                .build(),
				RequestBody.fromBytes(dataBytes)
		);

		return Utils.uploadedFileURI(s3Key);
	}

	/**
	 * Checks if the key exists in the S3 bucket.
	 *
	 * @param s3Key the key to check.
	 *
	 * @return true if the key exists in the S3 bucket, false otherwise.
	 */
	private boolean existsKeyInBucket(String s3Key) {
		HeadObjectResponse response;

		try {
			response = this.s3Client.headObject(
					HeadObjectRequest.builder()
					                 .bucket(S3_BUCKET_NAME)
					                 .key(s3Key)
					                 .build()
			);
		} catch (NoSuchKeyException ignored) {
			return false;
		}

		return response.sdkHttpResponse().isSuccessful();
	}

	/**
	 * Creates the key for a file to store in S3 bucket.
	 * Syntax is the following:
	 * {type}/{year}/{month}/{day}/{{@link RandomStringUtils#randomAlphanumeric(int) prefix}}_{fileName}
	 * <p>
	 * {@code {type}} must be one of the following:
	 * <ul>
	 *     <li>text</li>
	 *     <li>image</li>
	 *     <li>video</li>
	 *     <li>audio</li>
	 * </ul>
	 *
	 * @param fileName name of the file to upload to S3 bucket.
	 *
	 * @return the key for the image to store in S3 bucket.
	 */
	private String s3Key(String type, String fileName) {
		LocalDate date = LocalDate.now();

		return "%s/%s/%s/%s/(%s)_%s".formatted(
				type,
				date.getYear(),
				date.getMonthValue(),
				date.getDayOfMonth(),
				RandomStringUtils.randomAlphanumeric(15),
				fileName
		);
	}
}
