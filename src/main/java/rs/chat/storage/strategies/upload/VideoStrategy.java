package rs.chat.storage.strategies.upload;

import com.google.gson.JsonObject;
import lombok.extern.slf4j.Slf4j;
import org.mp4parser.IsoFile;
import org.mp4parser.boxes.iso14496.part12.MovieHeaderBox;
import org.mp4parser.tools.ByteBufferByteChannel;
import rs.chat.domain.entity.File;
import rs.chat.net.ws.Message;
import rs.chat.storage.S3;

import java.io.IOException;
import java.net.URI;

import static rs.chat.utils.Utils.bytesToUnit;

@Slf4j
public class VideoStrategy implements FileUploadStrategy {
	@Override
	public void handle(byte[] binaryData, String specificType, File file) throws IOException {
		JsonObject metadata = file.getMetadata();

		metadata.addProperty("duration", this.getVideoDuration(binaryData));
		metadata.addProperty("specificType", specificType);
		metadata.addProperty("size", bytesToUnit(binaryData.length));
		metadata.addProperty("messageType", Message.VIDEO_MESSAGE.type());

		URI uri = S3.getInstance().uploadFile(file.getType(), file.getName(), binaryData, metadata);

		file.setPath(uri.toString());
	}

	private String getVideoDuration(byte[] binaryData) {
		try (IsoFile isoFile = new IsoFile(new ByteBufferByteChannel(binaryData))) {
			MovieHeaderBox movieHeaderBox = isoFile.getMovieBox().getMovieHeaderBox();
			double preciseDuration = movieHeaderBox.getDuration() / (movieHeaderBox.getTimescale() / 1000d);
			int minutes = (int) (preciseDuration / 1000 / 60);
			int seconds = (int) (preciseDuration / 1000 % 60);

			return String.format("%02d:%02d", minutes, seconds);
		} catch (IOException e) {
			log.error("Could not parse video file ({})", e.getMessage());
		}

		return "Unknown";
	}
}
