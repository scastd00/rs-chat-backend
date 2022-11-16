package rs.chat.utils;

import java.io.IOException;

/**
 * A function that takes no parameters and returns a value.
 *
 * @param <R> the type of the return value.
 */
@FunctionalInterface
public interface NoParamFunction<R> {
	/**
	 * Applies the function.
	 *
	 * @return the result of the function.
	 *
	 * @throws IOException if an error occurs.
	 */
	R apply() throws IOException;
}
