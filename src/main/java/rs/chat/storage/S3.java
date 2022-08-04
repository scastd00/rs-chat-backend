package rs.chat.storage;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import rs.chat.net.ws.WSMessage;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.awscore.exception.AwsServiceException;
import software.amazon.awssdk.core.exception.SdkClientException;
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

@Slf4j
public class S3 {
	private static final S3 INSTANCE = new S3();
	private final S3Client s3Client;

	private S3() {
		this.s3Client = S3Client.builder()
		                        .endpointOverride(isDevEnv() ?
		                                          LOCAL_S3_ENDPOINT_URI :
		                                          REMOTE_S3_ENDPOINT_URI)
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

	public void checkS3BucketConnectivity() {
		HeadBucketResponse headBucketResponse = this.s3Client.headBucket(
				HeadBucketRequest.builder()
				                 .bucket(S3_BUCKET_NAME)
				                 .build()
		);

		SdkHttpResponse response = headBucketResponse.sdkHttpResponse();

		if (response.isSuccessful()) {
			log.debug("Successful HeadBucketResponse. Status code: {}", response.statusCode());
		}
	}

	public void uploadFile(String fileNameWithoutExtension, WSMessage messageType) {
		File file = messageType.buildFileInDisk(fileNameWithoutExtension);
		String s3Key = messageType.s3Key(fileNameWithoutExtension);

		this.s3Client.putObject(
				PutObjectRequest.builder()
				                .bucket(S3_BUCKET_NAME)
				                .key(s3Key)
				                .build(),
				RequestBody.fromFile(file)
		);

		try {
			Files.delete(file.toPath());
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@SneakyThrows(AwsServiceException.class)
	public File downloadFile(String fileNameWithoutExtension, WSMessage messageType) {
		File file = messageType.buildFileInDisk(fileNameWithoutExtension);
		String s3Key = messageType.s3Key(fileNameWithoutExtension);

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
		} catch (AwsServiceException | SdkClientException e) {
			throw new RuntimeException(e);
		}

		return response.sdkHttpResponse().isSuccessful();
	}
}
