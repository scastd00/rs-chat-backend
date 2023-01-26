package rs.chat.cache;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import rs.chat.storage.S3;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static rs.chat.utils.Constants.HISTORY_PAGE_SIZE;

/**
 * Class for caching the history file in memory. This file is cached in memory to
 * speed up the process of reading the messages. The cache is updated every time a new
 * message is going to be sent to the chat. The cache is used to read the history file and
 * send it to the client faster.
 */
@Getter
@Slf4j
public class CachedHistoryFile {
	private final String chatId;
	private final File file;
	private final List<String> history = new ArrayList<>();
	private final PrintWriter writer;
	private boolean closed = false;
	/**
	 * Lock object for synchronizing the access to the history cache. Used when reading the
	 * history file and when updating the cache.
	 */
	private final Object lock = new Object();

	/**
	 * Create a new instance of {@link CachedHistoryFile} and read the history file. When the
	 * constructor is called, the history file is downloaded from S3 and cached in memory.
	 * The history file is also opened for writing.
	 *
	 * @param chatId {@link String} with the chat ID.
	 *
	 * @throws IOException if an error occurs while reading the history file.
	 */
	public CachedHistoryFile(String chatId) throws IOException {
		this.chatId = chatId;
		this.file = S3.getInstance().downloadHistoryFile(chatId);

		try (FileReader fileReader = new FileReader(this.file)) {
			this.history.addAll(IOUtils.readLines(fileReader));
		} catch (FileNotFoundException e) {
			// Ignore
		}

		FileWriter fileWriter = new FileWriter(this.file, true);
		BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
		this.writer = new PrintWriter(bufferedWriter, true); // Auto-Flush enabled
	}

	/**
	 * Write a new message to the history file and update the cache.
	 *
	 * @param message {@link String} with the message to write.
	 */
	public void write(String message) {
		this.writer.println(message);

		synchronized (this.lock) {
			this.history.add(message);
		}
	}

	/**
	 * Get a page of the history file. The history file is read from the end to the beginning
	 * and the page is returned as a list of strings.
	 *
	 * @param page {@link Integer} with the page number.
	 *
	 * @return {@link List} of {@link String} with the messages of the page.
	 */
	public List<String> getPage(int page) {
		// Begin reading from the end of the list to the beginning
		synchronized (this.lock) {
			int size = this.history.size();
			int begin = size - (page * HISTORY_PAGE_SIZE);
			int end = begin + HISTORY_PAGE_SIZE;

			if (begin < 0) {
				begin = 0;
			}

			if (end > size) {
				end = size;
			}

			try {
				log.info("Getting page {} from {} to {}", page, begin, end);
				return this.history.subList(begin, end);
			} catch (IndexOutOfBoundsException | IllegalArgumentException e) {
				return Collections.emptyList();
			}
		}
	}

	/**
	 * Close the history file and mark it as closed.
	 */
	public synchronized void close() {
		if (this.closed) {
			return;
		}

		this.writer.close();
		this.closed = true;
	}
}
