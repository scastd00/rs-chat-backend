package rs.chat.controllers;

import com.google.gson.JsonObject;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import rs.chat.net.http.HttpRequest;
import rs.chat.net.http.HttpResponse;
import rs.chat.net.http.HttpResponse.HttpResponseBody;
import rs.chat.storage.S3;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.springframework.http.HttpStatus.OK;
import static rs.chat.router.Routes.PostRoute.UPLOAD_URL;

/**
 * Controller that manages all chat-related requests.
 */
@Slf4j
@RestController
@RequiredArgsConstructor
public class FileController {
	@PostMapping(UPLOAD_URL)
	public void uploadFile(HttpRequest request, HttpResponse response) throws IOException {
		log.info("Uploading file...");

		JsonObject body = request.body();
		String data = body.get("data")
		                  .getAsString()
		                  .split(",")[1];
		String fileName = body.get("name").getAsString();

		byte[] fileBytes = Base64.getDecoder().decode(data);
		BufferedImage image = ImageIO.read(new ByteArrayInputStream(fileBytes));

		Map<String, String> metadata = new HashMap<>();
		metadata.put("type", body.get("type").getAsString());
		metadata.put("width", String.valueOf(image.getWidth()));
		metadata.put("height", String.valueOf(image.getHeight()));
		metadata.put("size", String.valueOf(fileBytes.length));
		metadata.put("maxWidth", this.getMaxWidth(image.getWidth(), image.getHeight()));

		URI uri = S3.getInstance().uploadImage(
				URLEncoder.encode(fileName, UTF_8),
				fileBytes,
				metadata
		);

		HttpResponseBody responseBody = new HttpResponseBody("uri", uri);
		responseBody.add("name", fileName);
		responseBody.add("metadata", metadata);

		response.status(OK).send(responseBody);
		log.info("File uploaded successfully");
	}

	private String getMaxWidth(int width, int height) {
		if (width <= 200 || height <= 200) {
			return "xs";
		} else if (width <= 500 || height <= 500) {
			return "sm";
		} else if (width <= 800 || height <= 800) {
			return "md";
		} else if (width <= 1200 || height <= 1200) {
			return "lg";
		} else {
			return "xl";
		}
	}
}
