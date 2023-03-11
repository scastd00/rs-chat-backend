package rs.chat.ai.nsfw;

import com.google.gson.JsonArray;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.io.File;

import static rs.chat.utils.Constants.GSON;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Slf4j
public class NSFW {
	private static final RestTemplate restTemplate = new RestTemplate();

	public static boolean isNSFW(String filename, byte[] fileBytes) {
		String tmpDir = System.getProperty("java.io.tmpdir");
		String filePath = tmpDir + File.separator + filename;

		try {
			FileUtils.writeByteArrayToFile(new File(filePath), fileBytes);
			ResponseEntity<String> response = restTemplate.postForEntity(
					"http://localhost:4042/api/v1/nsfw/image",
					new ReqImage(filePath),
					String.class
			);

			JsonArray responseArray = GSON.fromJson(response.getBody(), JsonArray.class);
			NSFWResponse nsfwResponse = new NSFWResponse(responseArray);
		} catch (Exception e) {
			log.error("An error occurred while trying to determine if the file is NSFW.", e);
			throw new RuntimeException(e);
		}

		return false;
	}
}
