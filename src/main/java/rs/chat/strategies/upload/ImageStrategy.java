package rs.chat.strategies.upload;

import org.springframework.web.util.UriUtils;
import rs.chat.net.http.HttpResponse;
import rs.chat.net.http.HttpResponse.HttpResponseBody;
import rs.chat.storage.S3;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.springframework.http.HttpStatus.OK;

public class ImageStrategy implements FileUploadStrategy {
	@Override
	public void handle(byte[] binaryData, String name, String specificType, HttpResponse response) throws IOException {
		BufferedImage image = ImageIO.read(new ByteArrayInputStream(binaryData));

		Map<String, String> metadata = new HashMap<>();
		metadata.put("type", specificType);
		metadata.put("width", String.valueOf(image.getWidth()));
		metadata.put("height", String.valueOf(image.getHeight()));
		metadata.put("size", String.valueOf(binaryData.length));
		metadata.put("maxWidth", this.getMaxWidth(image.getWidth(), image.getHeight()));

		URI uri = S3.getInstance().uploadImage(
				UriUtils.encode(name, UTF_8),
				binaryData,
				metadata
		);

		HttpResponseBody responseBody = new HttpResponseBody("uri", uri);
		responseBody.add("name", name);
		responseBody.add("metadata", metadata);

		response.status(OK).send(responseBody);
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
