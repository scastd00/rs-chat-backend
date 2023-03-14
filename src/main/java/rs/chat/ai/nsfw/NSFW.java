package rs.chat.ai.nsfw;

import com.google.gson.JsonObject;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import rs.chat.ai.nsfw.NSFWResponse.ClassificationClass;

import static rs.chat.utils.Constants.GSON;
import static rs.chat.utils.Constants.NSFW_API_URL;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Slf4j
public class NSFW {
	private static final RestTemplate restTemplate = new RestTemplate();

	public static boolean isNSFW(String base64File, String endpoint) {
		try {
			ResponseEntity<String> response = restTemplate.postForEntity(
					NSFW_API_URL.resolve("/api/v1/nsfw/" + endpoint),
					new ReqImage(base64File),
					String.class
			);

			JsonObject responseObject = GSON.fromJson(response.getBody(), JsonObject.class);
			NSFWResponse nsfwResponse = new NSFWResponse(responseObject);

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
