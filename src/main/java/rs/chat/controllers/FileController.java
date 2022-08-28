package rs.chat.controllers;

import com.google.gson.JsonObject;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import rs.chat.exceptions.BadRequestException;
import rs.chat.net.http.HttpRequest;
import rs.chat.net.http.HttpResponse;
import rs.chat.strategies.upload.AudioStrategy;
import rs.chat.strategies.upload.FileUploadStrategy;
import rs.chat.strategies.upload.ImageStrategy;
import rs.chat.strategies.upload.TextStrategy;
import rs.chat.strategies.upload.VideoStrategy;

import java.io.IOException;
import java.util.Base64;

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
		String fileName = body.get("name").getAsString();
		String[] types = body.get("type").getAsString().split("/");
		String encodedData = body.get("data")
		                         .getAsString()
		                         .split(",")[1];

		byte[] fileBytes = Base64.getDecoder().decode(encodedData);

		FileUploadStrategy strategy = switch (types[0]) {
			case "image" -> new ImageStrategy();
			case "video" -> new VideoStrategy();
			case "audio" -> new AudioStrategy();
			case "text" -> new TextStrategy();
			default -> throw new BadRequestException("Invalid file uploaded");
		};

		strategy.handle(fileBytes, fileName, types[1], response);
		log.info("File uploaded successfully");
	}
}
