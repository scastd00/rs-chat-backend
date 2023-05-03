package rs.chat.mem.cache;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import rs.chat.exceptions.CacheException;

import java.util.concurrent.ExecutionException;

/**
 * Class that manages all the opened files when the chats are opened.
 */
public final class HistoryFilesCache {
	public static final HistoryFilesCache INSTANCE = new HistoryFilesCache();
	private final Cache<String, CachedHistoryFile> cache;

	/**
	 * Constructor that creates the cache.
	 */
	private HistoryFilesCache() {
		this.cache = CacheBuilder.newBuilder()
		                         .maximumSize(1000)
		                         .build();
	}

	/**
	 * Returns the cached file for the specified chatId or creates a new one if it doesn't exist.
	 *
	 * @param chatId id of the chat to get the file.
	 *
	 * @return the cached file for the specified chatId.
	 *
	 * @throws CacheException if the file cannot be created.
	 */
	public CachedHistoryFile get(String chatId) {
		try {
			return this.cache.get(chatId, () -> new CachedHistoryFile(chatId));
		} catch (ExecutionException e) {
			throw new CacheException(e.getMessage());
		}
	}

	/**
	 * Invalidates the cached file for the specified chatId.
	 *
	 * @param chatId id of the chat to invalidate the file.
	 */
	public void invalidate(String chatId) {
		this.cache.invalidate(chatId);
	}
}
