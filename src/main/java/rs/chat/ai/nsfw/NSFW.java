package rs.chat.ai.nsfw;

import com.google.gson.JsonArray;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import rs.chat.ai.nsfw.NSFWResponse.ClassificationClass;

import java.io.File;

import static rs.chat.utils.Constants.GSON;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Slf4j
public class NSFW {
	private static final RestTemplate restTemplate = new RestTemplate();

	public static boolean isNSFW(String filename, byte[] fileBytes, String endpoint) {
		String tmpDir = System.getProperty("java.io.tmpdir");
		String filePath = tmpDir + File.separator + filename;
		File file = new File(filePath);

		try {
			// Write the file to the temp directory. This is necessary because the NSFW
			// service only accepts files on the local file system.
			FileUtils.writeByteArrayToFile(file, fileBytes);
			ResponseEntity<String> response = restTemplate.postForEntity(
					"http://localhost:4042/api/v1/nsfw/" + endpoint,
					new ReqImage(filePath),
					String.class
			);

			JsonArray responseArray = GSON.fromJson(response.getBody(), JsonArray.class);
			NSFWResponse nsfwResponse = new NSFWResponse(responseArray);

			FileUtils.deleteQuietly(file); // Delete the file from the temp directory.

			return isVerySexyOrHentaiOrPorn(nsfwResponse);
		} catch (Exception e) {
			log.error("An error occurred while trying to determine if the file is NSFW.", e);
			throw new RuntimeException(e);
		}
	}

	private static boolean isVerySexyOrHentaiOrPorn(NSFWResponse nsfwResponse) {
		return nsfwResponse.probabilityOfClass(ClassificationClass.SEXY) >= 0.75 ||
				nsfwResponse.probabilityOfClass(ClassificationClass.PORN) >= 0.6 ||
				nsfwResponse.probabilityOfClass(ClassificationClass.HENTAI) >= 0.6;
	}
}
