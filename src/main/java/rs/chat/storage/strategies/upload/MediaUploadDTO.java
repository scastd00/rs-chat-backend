package rs.chat.storage.strategies.upload;

import rs.chat.domain.entity.File;

import java.util.Arrays;
import java.util.Objects;

public record MediaUploadDTO(byte[] binaryData, String specificType, File file) {
	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		MediaUploadDTO that = (MediaUploadDTO) o;
		return Arrays.equals(binaryData, that.binaryData) &&
				specificType.equals(that.specificType) &&
				file.equals(that.file);
	}

	@Override
	public int hashCode() {
		int result = Objects.hash(specificType, file);
		result = 31 * result + Arrays.hashCode(binaryData);
		return result;
	}

	@Override
	public String toString() {
		return "MediaUploadDTO{" +
				"binaryData=" + Arrays.toString(binaryData) +
				", specificType='" + specificType + '\'' +
				", file=" + file +
				'}';
	}
}
