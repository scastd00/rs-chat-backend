package rs.chat.strategies.upload;

import rs.chat.domain.entity.File;
import rs.chat.net.ws.Message;
import rs.chat.storage.S3;

import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import static rs.chat.utils.Constants.GSON;
import static rs.chat.utils.Utils.bytesToUnit;

public class PdfStrategy implements FileUploadStrategy {
	@Override
	public void handle(byte[] binaryData, String specificType, File file) throws IOException {
		Map<String, String> metadata = new HashMap<>();
		metadata.put("specificType", specificType);
		metadata.put("size", bytesToUnit(binaryData.length));
		metadata.put("messageType", Message.PDF_MESSAGE.type());

		URI uri = S3.getInstance().uploadFile(file.getType(), file.getName(), binaryData, metadata);

		file.setPath(uri.toString());
		file.setMetadata(GSON.toJson(metadata));
	}
}
