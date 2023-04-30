package rs.chat.mem;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class UnitConversion {
	/**
	 * Converts bytes to a human-readable unit.
	 *
	 * @param bytes the bytes to convert.
	 *
	 * @return the human-readable unit.
	 */
	public static String bytesToUnit(int bytes) {
		if (bytes < 1024) {
			return bytes + " B";
		} else if (bytes < 1024 * 1024) {
			return String.format("%.2f", bytes / 1024d) + " KB";
		} else if (bytes < 1024 * 1024 * 1024) {
			return String.format("%.2f", bytes / (1024d * 1024)) + " MB";
		} else {
			return String.format("%.2f", bytes / (1024d * 1024 * 1024)) + " GB";
		}
	}
}
