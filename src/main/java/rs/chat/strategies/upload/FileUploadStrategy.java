package rs.chat.strategies.upload;

import rs.chat.domain.entity.File;

import java.io.IOException;

public interface FileUploadStrategy {
	void handle(byte[] binaryData, String specificType, File file) throws IOException;
}
