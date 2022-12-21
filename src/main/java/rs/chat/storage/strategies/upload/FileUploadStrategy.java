package rs.chat.storage.strategies.upload;

import rs.chat.domain.entity.File;

import java.io.IOException;

public interface FileUploadStrategy {
	/**
	 * Performs all the necessary operations to upload a file.
	 *
	 * @param binaryData   The data of the file.
	 * @param specificType MIME specific type of the file.
	 * @param file         The file entity to save.
	 *
	 * @throws IOException If an I/O error occurs.
	 */
	void handle(byte[] binaryData, String specificType, File file) throws IOException;
}
