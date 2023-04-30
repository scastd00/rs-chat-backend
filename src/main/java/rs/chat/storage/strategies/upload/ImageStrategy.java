package rs.chat.storage.strategies.upload;

import com.google.gson.JsonObject;
import rs.chat.net.ws.Message;
import rs.chat.storage.S3;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URI;

import static rs.chat.mem.UnitConversion.bytesToUnit;

public class ImageStrategy implements FileUploadStrategy {
	@Override
	public void handle(MediaUploadDTO mediaUploadDTO) throws IOException {
		BufferedImage image = ImageIO.read(new ByteArrayInputStream(mediaUploadDTO.binaryData()));

		JsonObject metadata = mediaUploadDTO.file().getMetadata();
		metadata.addProperty("specificType", mediaUploadDTO.specificType());
		metadata.addProperty("width", String.valueOf(image.getWidth()));
		metadata.addProperty("height", String.valueOf(image.getHeight()));
		metadata.addProperty("size", bytesToUnit(mediaUploadDTO.binaryData().length));
		metadata.addProperty("maxWidth", this.getMaxWidth(image.getWidth(), image.getHeight()));
		metadata.addProperty("messageType", Message.IMAGE_MESSAGE.type());

		URI uri = S3.getInstance().uploadFile(
				mediaUploadDTO.file().getType(),
				mediaUploadDTO.file().getName(),
				mediaUploadDTO.binaryData(),
				metadata
		);

		mediaUploadDTO.file().setPath(uri.toString());
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
