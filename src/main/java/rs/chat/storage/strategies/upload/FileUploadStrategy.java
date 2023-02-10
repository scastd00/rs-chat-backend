package rs.chat.storage.strategies.upload;

import java.io.IOException;

public interface FileUploadStrategy {
	/**
	 * Performs all the necessary operations to upload a file. This method should modify the next
	 * file attributes:
	 * <ul>
	 *     <li>path</li>
	 *     <li>metadata</li>
	 * </ul>
	 *
	 * @param mediaUploadDTO DTO that contains the file's binary data, specific type and file.
	 *
	 * @throws IOException If an I/O error occurs.
	 */
	void handle(MediaUploadDTO mediaUploadDTO) throws IOException;
}
