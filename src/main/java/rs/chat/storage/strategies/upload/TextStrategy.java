package rs.chat.storage.strategies.upload;

import com.google.gson.JsonObject;
import rs.chat.domain.entity.File;
import rs.chat.net.ws.Message;
import rs.chat.storage.S3;

import java.io.IOException;
import java.net.URI;

import static rs.chat.utils.Utils.bytesToUnit;

public class TextStrategy implements FileUploadStrategy {
	@Override
	public void handle(byte[] binaryData, String specificType, File file) throws IOException {
		JsonObject metadata = file.getMetadata();
		metadata.addProperty("specificType", specificType);
		metadata.addProperty("size", bytesToUnit(binaryData.length));
		metadata.addProperty("messageType", Message.TEXT_DOC_MESSAGE.type());

		URI uri = S3.getInstance().uploadFile(file.getType(), file.getName(), binaryData, metadata);

		file.setPath(uri.toString());
	}
}
