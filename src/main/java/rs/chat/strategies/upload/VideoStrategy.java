package rs.chat.strategies.upload;

import lombok.extern.slf4j.Slf4j;
import org.mp4parser.IsoFile;
import org.mp4parser.boxes.iso14496.part12.MovieHeaderBox;
import org.mp4parser.tools.ByteBufferByteChannel;
import org.springframework.http.HttpStatus;
import rs.chat.net.http.HttpResponse;
import rs.chat.net.http.HttpResponse.HttpResponseBody;
import rs.chat.net.ws.WSMessage;
import rs.chat.storage.S3;

import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import static rs.chat.utils.Utils.bytesToUnit;

@Slf4j
public class VideoStrategy implements FileUploadStrategy {
	@Override
	public void handle(byte[] binaryData, String name, String specificType, HttpResponse response) throws IOException {
		Map<String, String> metadata = new HashMap<>();

		try (IsoFile isoFile = new IsoFile(new ByteBufferByteChannel(binaryData))) {
			MovieHeaderBox movieHeaderBox = isoFile.getMovieBox().getMovieHeaderBox();
			double preciseDuration = movieHeaderBox.getDuration() / (movieHeaderBox.getTimescale() / 1000d);
			int minutes = (int) (preciseDuration / 1000 / 60);
			int seconds = (int) (preciseDuration / 1000 % 60);
			metadata.put("duration", String.format("%02d:%02d", minutes, seconds));
		} catch (IOException e) {
			log.error("Could not parse video file ({})", e.getMessage());
			metadata.put("duration", "Unknown");
		}

		metadata.put("type", specificType);
		metadata.put("size", bytesToUnit(binaryData.length));
		metadata.put("messageType", WSMessage.VIDEO_MESSAGE.type());

		URI uri = S3.getInstance().uploadVideo(name, binaryData, metadata);

		HttpResponseBody responseBody = new HttpResponseBody("uri", uri);
		responseBody.add("name", name);
		responseBody.add("metadata", metadata);

		response.status(HttpStatus.OK).send(responseBody);
	}
}
