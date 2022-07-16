package rs.chat.storage;

import lombok.extern.slf4j.Slf4j;
import rs.chat.utils.Constants;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.ListObjectsRequest;
import software.amazon.awssdk.services.s3.model.ListObjectsResponse;
import software.amazon.awssdk.services.s3.model.S3Object;

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
}
