package rs.chat.controllers;

import com.google.gson.JsonObject;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import rs.chat.domain.entity.File;
import rs.chat.exceptions.BadRequestException;
import rs.chat.net.http.HttpRequest;
import rs.chat.net.http.HttpResponse;
import rs.chat.net.http.HttpResponse.HttpResponseBody;
import rs.chat.service.FileService;
import rs.chat.strategies.upload.AudioStrategy;
import rs.chat.strategies.upload.FileUploadStrategy;
import rs.chat.strategies.upload.ImageStrategy;
import rs.chat.strategies.upload.TextStrategy;
import rs.chat.strategies.upload.VideoStrategy;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.Base64;

import static org.springframework.http.HttpStatus.OK;
import static rs.chat.router.Routes.PostRoute.UPLOAD_URL;

/**
 * Controller that manages all chat-related requests.
 */
@Slf4j
@RestController
@RequiredArgsConstructor
public class FileController {
	private final FileService fileService;

	@PostMapping(UPLOAD_URL)
	public void uploadFile(HttpRequest request, HttpResponse response) throws IOException {
		log.info("Uploading file...");

		JsonObject body = request.body();
		Long userId = body.get("userId").getAsLong(); // Todo: receive it from the client
		String fileName = body.get("name").getAsString().replace(" ", "_");
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

		File fileToSave = new File();
		fileToSave.setId(null);
		fileToSave.setName(fileName);
		fileToSave.setDateUploaded(new Timestamp(System.currentTimeMillis()));
		fileToSave.setSize(fileBytes.length);
		fileToSave.setType(types[0]);
		fileToSave.setUserId(userId);

		strategy.handle(fileBytes, types[1], fileToSave);

		this.fileService.save(fileToSave);

		HttpResponseBody responseBody = new HttpResponseBody("uri", fileToSave.getPath());
		responseBody.add("name", fileName);
		responseBody.add("metadata", fileToSave.getMetadata());

		// Todo: fix the names in frontend

		response.status(OK).send(responseBody);
		log.info("File uploaded successfully");
	}
}
