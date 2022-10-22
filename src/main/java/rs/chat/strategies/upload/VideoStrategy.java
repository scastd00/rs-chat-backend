package rs.chat.strategies.upload;

import lombok.extern.slf4j.Slf4j;
import org.mp4parser.IsoFile;
import org.mp4parser.boxes.iso14496.part12.MovieHeaderBox;
import org.mp4parser.tools.ByteBufferByteChannel;
import rs.chat.domain.entity.File;
import rs.chat.net.ws.WSMessage;
import rs.chat.storage.S3;

import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import static rs.chat.utils.Constants.GSON;
import static rs.chat.utils.Utils.bytesToUnit;

@Slf4j
public class VideoStrategy implements FileUploadStrategy {
	@Override
	public void handle(byte[] binaryData, String specificType, File file) throws IOException {
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

		metadata.put("specificType", specificType);
		metadata.put("size", bytesToUnit(binaryData.length));
		metadata.put("messageType", WSMessage.VIDEO_MESSAGE.type());

		URI uri = S3.getInstance().uploadFile(file.getType(), file.getName(), binaryData, metadata);

		file.setPath(uri.toString());
		file.setMetadata(GSON.toJson(metadata));
	}
}
