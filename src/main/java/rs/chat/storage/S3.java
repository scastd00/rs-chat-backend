package rs.chat.storage;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import rs.chat.net.ws.WSMessage;
import rs.chat.utils.Constants;
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

	public void uploadFile(String fileNameWithoutExtension, WSMessage messageType) {
		File file = messageType.buildFileInDisk(fileNameWithoutExtension);

		this.s3Client.putObject(
				PutObjectRequest.builder()
				                .bucket(S3_BUCKET_NAME)
				                .key(messageType.s3Key(fileNameWithoutExtension))
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

		if (this.existsKeyInBucket(s3Key, messageType)) {
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

	private boolean existsKeyInBucket(String key, WSMessage messageType) {
		HeadObjectResponse response;

		try {
			response = this.s3Client.headObject(
					HeadObjectRequest.builder()
					                 .bucket(S3_BUCKET_NAME)
					                 .key(key)
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
