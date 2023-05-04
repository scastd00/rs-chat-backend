package rs.chat.ai.nsfw;

import com.google.gson.JsonObject;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import rs.chat.ai.nsfw.NSFWResponse.NSFWClass;
import rs.chat.json.JsonParser;

import static rs.chat.Constants.NSFW_API_URL;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Slf4j
@Component
public class NSFWService {
	private static final RestTemplate restTemplate = new RestTemplate();

	public boolean isNSFW(String base64File, String endpoint) {
		try {
			ResponseEntity<String> response = restTemplate.postForEntity(
					NSFW_API_URL.resolve("/api/v1/nsfw/" + endpoint),
					new NSFWRequestImage(base64File),
					String.class
			);

			JsonObject responseObject = JsonParser.parseJson(response.getBody());
			NSFWResponse nsfwResponse = new NSFWResponse(responseObject);

			return isVerySexyOrHentaiOrPorn(nsfwResponse);
		} catch (Exception e) {
			log.error("An error occurred while trying to determine if the file is NSFW.", e);
			throw new RuntimeException(e);
		}
	}

	private boolean isVerySexyOrHentaiOrPorn(NSFWResponse nsfwResponse) {
		return nsfwResponse.probabilityOfClass(NSFWClass.SEXY) >= 0.75 ||
				nsfwResponse.probabilityOfClass(NSFWClass.PORN) >= 0.6 ||
				nsfwResponse.probabilityOfClass(NSFWClass.HENTAI) >= 0.6;
	}
}
