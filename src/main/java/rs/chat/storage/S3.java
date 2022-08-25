package rs.chat.storage;

import lombok.extern.slf4j.Slf4j;
import rs.chat.net.ws.WSMessage;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.core.exception.SdkException;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.core.sync.ResponseTransformer;
import software.amazon.awssdk.http.SdkHttpResponse;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.HeadBucketRequest;
import software.amazon.awssdk.services.s3.model.HeadBucketResponse;
import software.amazon.awssdk.services.s3.model.HeadObjectRequest;
import software.amazon.awssdk.services.s3.model.HeadObjectResponse;
import software.amazon.awssdk.services.s3.model.NoSuchKeyException;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import static rs.chat.utils.Constants.LOCAL_S3_ENDPOINT_URI;
import static rs.chat.utils.Constants.REMOTE_S3_ENDPOINT_URI;
import static rs.chat.utils.Constants.S3_BUCKET_NAME;
import static rs.chat.utils.Utils.isDevEnv;

/**
 * Class that provides utility methods to work with S3.
 */
@Slf4j
public class S3 {
	private static final S3 INSTANCE = new S3();
	private final S3Client s3Client;

	/**
	 * Private constructor that initializes the S3 client.
	 */
	private S3() {
		this.s3Client = S3Client.builder()
		                        .endpointOverride(isDevEnv() ?
		                                          LOCAL_S3_ENDPOINT_URI :
		                                          REMOTE_S3_ENDPOINT_URI)
		                        .credentialsProvider(this::obtainCredentials)
		                        .region(Region.EU_WEST_3)
		                        .build();
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
		HeadBucketResponse headBucketResponse = this.s3Client.headBucket(
				HeadBucketRequest.builder()
				                 .bucket(S3_BUCKET_NAME)
				                 .build()
		);

		SdkHttpResponse response = headBucketResponse.sdkHttpResponse();

		if (response.isSuccessful()) {
			log.debug("Successful HeadBucketResponse. Status code: {}", response.statusCode());
		} else {
			log.error("HeadBucketResponse failed. Status code: {}", response.statusCode());
			throw SdkException.create("HeadBucketResponse failed. Status code: " + response.statusCode(), null);
		}
	}

	/**
	 * Uploads a chat file to S3 bucket.
	 *
	 * @param chatId      the chat id of the chat file to upload to S3 bucket.
	 * @param messageType the message type of the chat file to upload to S3 bucket.
	 */
	public void uploadFile(String chatId, WSMessage messageType) {
		File file = messageType.buildFileInDisk(chatId);
		String s3Key = messageType.s3Key(chatId);

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
	}

	/**
	 * Downloads a chat file from S3 bucket.
	 *
	 * @param chatId      the chat id of the chat file to download from S3 bucket.
	 * @param messageType the message type of the chat file to download from S3 bucket.
	 *
	 * @return the downloaded chat file.
	 *
	 * @throws SdkException if there is an error with the S3 client.
	 */
	public File downloadFile(String chatId, WSMessage messageType) {
		File file = messageType.buildFileInDisk(chatId);
		String s3Key = messageType.s3Key(chatId);

		if (this.existsKeyInBucket(s3Key)) {
			this.s3Client.getObject(
					GetObjectRequest.builder()
					                .bucket(S3_BUCKET_NAME)
					                .key(s3Key)
					                .build(),
					ResponseTransformer.toFile(file)
			);
		}

		return file;
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
}
