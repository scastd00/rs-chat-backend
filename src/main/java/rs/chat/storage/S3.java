package rs.chat.storage;

import com.google.gson.JsonObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import rs.chat.exceptions.CouldNotUploadFileException;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.core.exception.SdkException;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.core.sync.ResponseTransformer;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.CORSRule;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.HeadObjectRequest;
import software.amazon.awssdk.services.s3.model.HeadObjectResponse;
import software.amazon.awssdk.services.s3.model.NoSuchKeyException;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.time.Clock;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import static rs.chat.net.ws.Message.GET_HISTORY_MESSAGE;
import static rs.chat.net.ws.Message.TEXT_MESSAGE;
import static rs.chat.utils.Constants.S3_BUCKET_NAME;
import static rs.chat.utils.Constants.S3_ENDPOINT_URI;
import static rs.chat.utils.Constants.S3_ENDPOINT_URI_FOR_FILES;

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
		                        .endpointOverride(S3_ENDPOINT_URI)
		                        .credentialsProvider(this::obtainCredentials)
		                        .region(Region.EU_WEST_3)
		                        .build();
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
	 * Checks the connectivity to the S3 bucket and creates it if it does not exist.
	 *
	 * @throws SdkException if there is an error while creating the bucket or setting the CORS rules.
	 */
	public void checkS3BucketConnectivity() {
		if (!this.existsBucket()) {
			this.s3Client.createBucket(b -> b.bucket(S3_BUCKET_NAME));
			log.debug("Created S3 bucket {}", S3_BUCKET_NAME);
		}

		// Set CORS rules for the bucket.
		this.configureCorsInBucket();
	}

	/**
	 * Checks if the S3 bucket exists.
	 *
	 * @return {@code true} if the bucket exists, {@code false} otherwise.
	 */
	private boolean existsBucket() {
		try {
			return this.s3Client.listBuckets()
			                    .buckets()
			                    .stream()
			                    .anyMatch(b -> b.name().equals(S3_BUCKET_NAME));
		} catch (SdkException e) {
			return false;
		}
	}

	/**
	 * Uploads a chat history file to S3 bucket and deletes it from the local disk.
	 *
	 * @param chatId     the chat id of the chat history file to upload to S3 bucket.
	 * @param removeFile whether to delete the file from the local disk after it has been uploaded to S3.
	 */
	public void uploadHistoryFile(String chatId, boolean removeFile) {
		File file = GET_HISTORY_MESSAGE.getFileInDisk(chatId);
		String s3Key = GET_HISTORY_MESSAGE.s3Key(chatId);

		this.s3Client.putObject(
				PutObjectRequest.builder()
				                .bucket(S3_BUCKET_NAME)
				                .key(s3Key)
				                .build(),
				RequestBody.fromFile(file)
		);

		if (removeFile) {
			try {
				// Delete the file from the local disk after it has been uploaded to S3.
				Files.delete(file.toPath());
			} catch (IOException e) {
				throw new CouldNotUploadFileException(e.getMessage());
			}
		}

		log.debug("Uploaded file {} to S3 bucket with key {}", file.getName(), s3Key);
	}

	/**
	 * Downloads a chat history file from S3 bucket and saves it to the local disk.
	 *
	 * @param chatId the chat id of the chat history file to download from S3 bucket.
	 *
	 * @return the downloaded chat history file.
	 */
	public File downloadHistoryFile(String chatId) {
		File file = GET_HISTORY_MESSAGE.getFileInDisk(chatId);
		String s3Key = GET_HISTORY_MESSAGE.s3Key(chatId);

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
	public URI uploadFile(String mediaType, String fileName, byte[] dataBytes, JsonObject metadata) {
		String s3Key = this.s3Key(mediaType, fileName);

		Map<String, String> metadataMap = new HashMap<>();
		metadata.asMap().forEach((key, value) -> metadataMap.put(key, value.toString()));

		this.s3Client.putObject(
				PutObjectRequest.builder()
				                .bucket(S3_BUCKET_NAME)
				                .key(s3Key)
				                .metadata(metadataMap)
				                .build(),
				RequestBody.fromBytes(dataBytes)
		);

		return uploadedFileURI(s3Key);
	}

	/**
	 * Creates a new {@link URI} for the specified S3 key. See
	 * {@link rs.chat.utils.Constants#S3_ENDPOINT_URI_FOR_FILES} to see the base URI.
	 *
	 * @param s3Key the S3 key to create the {@link URI} for.
	 *
	 * @return the {@link URI} for the specified S3 key.
	 */
	public static URI uploadedFileURI(String s3Key) {
		return S3_ENDPOINT_URI_FOR_FILES.resolve(s3Key);
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
	 * Syntax is the following: {type}/{year}/{month}/{day}/{prefix}_{fileName}
	 * <p>
	 * {@code {type}} must be one of the following:
	 * <ul>
	 *     <li>text</li>
	 *     <li>image</li>
	 *     <li>video</li>
	 *     <li>audio</li>
	 * </ul>
	 * <p>
	 * Prefix is a random generated string of 15 characters (see {@link RandomStringUtils#randomAlphanumeric(int)}).
	 *
	 * @param fileName name of the file to upload to S3 bucket.
	 *
	 * @return the key for the image to store in S3 bucket.
	 */
	private String s3Key(String type, String fileName) {
		LocalDate date = LocalDate.now(Clock.systemUTC());

		return "%s/%s/%s/%s/(%s)_%s".formatted(
				type,
				date.getYear(),
				date.getMonthValue(),
				date.getDayOfMonth(),
				RandomStringUtils.randomAlphanumeric(15),
				fileName
		);
	}

	/**
	 * Configures CORS in S3 bucket. This is needed for the web client to be able to access
	 * the files in S3 bucket.
	 */
	private void configureCorsInBucket() {
		CORSRule corsRule = CORSRule.builder()
		                            .allowedHeaders("*")
		                            .allowedMethods("GET", "POST", "PUT", "DELETE", "HEAD")
		                            .allowedOrigins("*")
		                            .id("RSChat-AllowAllOrigins")
		                            .build();

		this.s3Client.putBucketCors(
				b -> b.bucket(S3_BUCKET_NAME).corsConfiguration(builder -> builder.corsRules(corsRule))
		);

		log.debug("Configured CORS in S3 bucket {}", S3_BUCKET_NAME);
	}
}
