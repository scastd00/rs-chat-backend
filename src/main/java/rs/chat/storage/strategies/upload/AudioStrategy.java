package rs.chat.storage.strategies.upload;

import com.google.gson.JsonObject;
import rs.chat.net.ws.Message;
import rs.chat.storage.S3;

import java.io.IOException;
import java.net.URI;

import static rs.chat.mem.UnitConversion.bytesToUnit;

public class AudioStrategy implements FileUploadStrategy {
	@Override
	public void handle(MediaUploadDTO mediaUploadDTO) throws IOException {
		JsonObject metadata = mediaUploadDTO.file().getMetadata();
		metadata.addProperty("specificType", mediaUploadDTO.specificType());
		metadata.addProperty("size", bytesToUnit(mediaUploadDTO.binaryData().length));
		metadata.addProperty("messageType", Message.AUDIO_MESSAGE.type());

		URI uri = S3.getInstance().uploadFile(
				mediaUploadDTO.file().getType(),
				mediaUploadDTO.file().getName(),
				mediaUploadDTO.binaryData(),
				metadata
		);

		mediaUploadDTO.file().setPath(uri.toString());
	}
}
