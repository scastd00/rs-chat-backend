package rs.chat.strategies.upload;

import rs.chat.domain.entity.File;
import rs.chat.net.ws.WSMessage;
import rs.chat.storage.S3;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import static rs.chat.utils.Constants.GSON;
import static rs.chat.utils.Utils.bytesToUnit;

public class ImageStrategy implements FileUploadStrategy {
	@Override
	public void handle(byte[] binaryData, String specificType, File file) throws IOException {
		BufferedImage image = ImageIO.read(new ByteArrayInputStream(binaryData));

		Map<String, String> metadata = new HashMap<>();
		metadata.put("specificType", specificType);
		metadata.put("width", String.valueOf(image.getWidth()));
		metadata.put("height", String.valueOf(image.getHeight()));
		metadata.put("size", bytesToUnit(binaryData.length));
		metadata.put("maxWidth", this.getMaxWidth(image.getWidth(), image.getHeight()));
		metadata.put("messageType", WSMessage.IMAGE_MESSAGE.type());

		URI uri = S3.getInstance().uploadFile(file.getType(), file.getName(), binaryData, metadata);

		file.setPath(uri.toString());
		file.setMetadata(GSON.toJson(metadata));
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
		}

		return "xl";
	}
}
