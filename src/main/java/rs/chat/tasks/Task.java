package rs.chat.tasks;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Interface for performing custom tasks in the server.
 * Some fields in the implementing classes may have the {@link lombok.Setter} annotation, to
 * allow adding attributes after the object has been created.
 */
public interface Task {
	void run() throws TaskExecutionException;

	/**
	 * Class for returning the status of a task. The status is an integer, and the message is a
	 * {@link String} that can be used to describe the status. It is recommended to use the
	 * {@link TaskStatus#builder()} method to create a new instance of this class.
	 */
	@Builder
	@Getter
	@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
	class TaskStatus {
		public static final int SUCCESS = 0;
		public static final int FAILURE = 1;
		public static final int WARNING = 2;
		public static final int FATAL = 3;

		private final int status;
		private final String message;
	}
}
