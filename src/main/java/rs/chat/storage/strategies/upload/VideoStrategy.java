package rs.chat.storage.strategies.upload;

import com.google.gson.JsonObject;
import lombok.extern.slf4j.Slf4j;
import org.mp4parser.IsoFile;
import org.mp4parser.boxes.iso14496.part12.MovieHeaderBox;
import org.mp4parser.tools.ByteBufferByteChannel;
import rs.chat.net.ws.Message;
import rs.chat.storage.S3;

import java.io.IOException;
import java.net.URI;

import static rs.chat.mem.UnitConversion.bytesToUnit;

@Slf4j
public class VideoStrategy implements FileUploadStrategy {
	@Override
	public void handle(MediaUploadDTO mediaUploadDTO) throws IOException {
		JsonObject metadata = mediaUploadDTO.file().getMetadata();

		metadata.addProperty("duration", this.getVideoDuration(mediaUploadDTO.binaryData()));
		metadata.addProperty("specificType", mediaUploadDTO.specificType());
		metadata.addProperty("size", bytesToUnit(mediaUploadDTO.binaryData().length));
		metadata.addProperty("messageType", Message.VIDEO_MESSAGE.type());

		URI uri = S3.getInstance().uploadFile(
				mediaUploadDTO.file().getType(),
				mediaUploadDTO.file().getName(),
				mediaUploadDTO.binaryData(),
				metadata
		);

		mediaUploadDTO.file().setPath(uri.toString());
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
