package rs.chat.controllers;

import com.google.gson.JsonObject;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import rs.chat.domain.entity.File;
import rs.chat.domain.entity.dtos.FileDto;
import rs.chat.domain.service.FileService;
import rs.chat.domain.service.UserService;
import rs.chat.exceptions.BadRequestException;
import rs.chat.exceptions.CouldNotUploadFileException;
import rs.chat.net.http.HttpRequest;
import rs.chat.net.http.HttpResponse;
import rs.chat.storage.strategies.upload.FileUploadStrategy;
import rs.chat.storage.strategies.upload.UploadMappings;

import java.io.IOException;
import java.time.Clock;
import java.time.Instant;
import java.util.Base64;

import static rs.chat.router.Routes.PostRoute.UPLOAD_URL;
import static rs.chat.utils.Constants.MAX_FILE_BYTES;

/**
 * Controller that manages all chat-related requests.
 */
@Slf4j
@RestController
@RequiredArgsConstructor
public class FileController {
	private final FileService fileService;
	private final UserService userService;
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
		String mimeType = file.get("type").getAsString();
		String[] mimeTypes = mimeType.split("/");
		String encodedData = file.get("data").getAsString().split(",")[1];

		FileDto fileDto = ControllerUtils.performActionThatMayThrowException(response, () -> {
			byte[] fileBytes = Base64.getDecoder().decode(encodedData);

			if (fileBytes.length == 0) {
				throw new BadRequestException("File is empty");
			} else if (fileBytes.length > MAX_FILE_BYTES) {
				throw new BadRequestException("File is too big");
			}

			File fileToSave = new File(
					null,
					fileName,
					Instant.now(this.clock),
					fileBytes.length,
					"",
					"",
					mimeTypes[0].toUpperCase(),
					this.userService.getUserById(userId)
			);

			try {
				FileUploadStrategy strategy = UploadMappings.getStrategy(mimeType);
				log.info("Uploading file ({}) with strategy ({})", fileName, strategy.getClass().getSimpleName());
				strategy.handle(fileBytes, mimeTypes[1], fileToSave); // Modifies the fileToSave object
			} catch (IOException e) {
				throw new CouldNotUploadFileException(e.getMessage());
			}

			return this.fileService.save(fileToSave);
		});

		response.ok().send(fileDto);
		log.info("File ({}) uploaded successfully", fileName);
	}
}
