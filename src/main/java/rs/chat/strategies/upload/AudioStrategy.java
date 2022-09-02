package rs.chat.strategies.upload;

import org.springframework.http.HttpStatus;
import rs.chat.net.http.HttpResponse;
import rs.chat.net.http.HttpResponse.HttpResponseBody;
import rs.chat.net.ws.WSMessage;
import rs.chat.storage.S3;

import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

public class AudioStrategy implements FileUploadStrategy {
	@Override
	public void handle(byte[] binaryData, String name, String specificType, HttpResponse response) throws IOException {
		Map<String, String> metadata = new HashMap<>();
		metadata.put("type", specificType);
		metadata.put("size", String.valueOf(binaryData.length));
		metadata.put("messageType", WSMessage.AUDIO_MESSAGE.type());

		URI uri = S3.getInstance().uploadAudio(name, binaryData, metadata);

		HttpResponseBody responseBody = new HttpResponseBody("uri", uri);
		responseBody.add("name", name);
		responseBody.add("metadata", metadata);

		response.status(HttpStatus.OK).send(responseBody);
	}
}
