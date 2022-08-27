package rs.chat.controllers;

import com.google.gson.JsonObject;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import rs.chat.net.http.HttpRequest;
import rs.chat.net.http.HttpResponse;
import rs.chat.storage.S3;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.util.Base64;
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

		URI uri = S3.getInstance().uploadImage(
				URLEncoder.encode(body.get("name").getAsString(), UTF_8),
				Base64.getDecoder().decode(data),
				Map.of("type", body.get("type").getAsString())
		);

		response.status(OK).send("uri", uri);
		log.info("File uploaded successfully");
	}
}
