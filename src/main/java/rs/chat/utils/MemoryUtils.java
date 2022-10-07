package rs.chat.utils;

/**
 * Utility class for memory related operations.
 */
public final class MemoryUtils {
	private MemoryUtils() {
	}

	/**
	 * @return the current memory usage in bytes.
	 */
	public static long getMemoryUsage() {
		return Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
	}

	/**
	 * @return the current memory usage in megabytes.
	 */
	public static double getMemoryUsageInMB() {
		return getMemoryUsage() / 1024.0 / 1024.0;
	}
}
