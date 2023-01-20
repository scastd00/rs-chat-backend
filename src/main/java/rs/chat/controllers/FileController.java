package rs.chat.controllers;

import com.google.gson.JsonObject;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.unit.DataSize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import rs.chat.domain.entity.File;
import rs.chat.domain.service.FileService;
import rs.chat.exceptions.BadRequestException;
import rs.chat.net.http.HttpRequest;
import rs.chat.net.http.HttpResponse;
import rs.chat.net.http.HttpResponse.HttpResponseBody;
import rs.chat.storage.strategies.upload.AudioStrategy;
import rs.chat.storage.strategies.upload.FileUploadStrategy;
import rs.chat.storage.strategies.upload.ImageStrategy;
import rs.chat.storage.strategies.upload.PdfStrategy;
import rs.chat.storage.strategies.upload.TextStrategy;
import rs.chat.storage.strategies.upload.VideoStrategy;

import java.io.IOException;
import java.time.Clock;
import java.time.Instant;
import java.util.Base64;

import static rs.chat.router.Routes.PostRoute.UPLOAD_URL;

/**
 * Controller that manages all chat-related requests.
 */
@Slf4j
@RestController
@RequiredArgsConstructor
public class FileController {
	private final FileService fileService;
	private final Clock clock;

	/**
	 * Uploads a file to the server.
	 *
	 * @param request  request that contains the file.
	 * @param response response that will be sent to the client.
	 *
	 * @throws IOException if an I/O error occurs.
	 */
	@PostMapping(UPLOAD_URL)
	public void uploadFile(HttpRequest request, HttpResponse response) throws IOException {
		JsonObject body = request.body();

		Long userId = body.get("userId").getAsLong();
		JsonObject file = body.get("file").getAsJsonObject();

		String fileName = file.get("name").getAsString().replace(" ", "_");
		String[] mimeTypes = file.get("type").getAsString().split("/");
		String encodedData = file.get("data")
		                         .getAsString()
		                         .split(",")[1];

		byte[] fileBytes = Base64.getDecoder().decode(encodedData);

		if (fileBytes.length == 0) {
			throw new BadRequestException("File is empty");
		} else if (fileBytes.length > DataSize.ofMegabytes(100).toBytes()) {
			throw new BadRequestException("File is too big");
		}

		log.info("Uploading file...");

		FileUploadStrategy strategy = switch (mimeTypes[0]) {
			case "image" -> new ImageStrategy();
			case "video" -> new VideoStrategy();
			case "audio" -> new AudioStrategy();
			case "text" -> new TextStrategy();
			case "application" -> {
				if (mimeTypes[1].equals("pdf")) {
					yield new PdfStrategy();
				} else {
					throw new BadRequestException("Unsupported application file type");
				}
			}
			default -> throw new BadRequestException("Invalid file uploaded");
		};

		File fileToSave = new File(
				null,
				fileName,
				Instant.now(this.clock),
				fileBytes.length,
				null,
				null,
				mimeTypes[0],
				userId
		);

		strategy.handle(fileBytes, mimeTypes[1], fileToSave); // Modifies the fileToSave object

		this.fileService.save(fileToSave);

		HttpResponseBody responseBody = new HttpResponseBody("uri", fileToSave.getPath());
		responseBody.add("name", fileName);
		responseBody.add("metadata", fileToSave.getMetadata());

		response.ok().send(responseBody);
		log.info("File ({}) uploaded successfully", fileName);
	}
}
