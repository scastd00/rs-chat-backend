package rs.chat.strategies.upload;

import rs.chat.net.http.HttpResponse;

import java.io.IOException;

public class VideoStrategy implements FileUploadStrategy {
	@Override
	public void handle(byte[] binaryData, String name, String specificType, HttpResponse response) throws IOException {

	}
}
