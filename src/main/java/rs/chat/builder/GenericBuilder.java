package rs.chat.builder;

/**
 * Interface for builders.
 *
 * @param <T> the type of the object to build.
 */
public interface GenericBuilder<T> {
	/**
	 * Builds the object.
	 *
	 * @return the built object.
	 */
	T build();
}
