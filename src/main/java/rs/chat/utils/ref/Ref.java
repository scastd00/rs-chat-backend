package rs.chat.utils.ref;

import lombok.NonNull;

import java.util.function.Supplier;

/**
 * Reference class to hold a value that can change in time.
 * Also provides a reset function to reset the value to its initial value.
 *
 * @param <T> the type of the value.
 */
public final class Ref<T> {
	private T value;
	private final Supplier<T> resetFn;

	/**
	 * Creates a new reference with the given value.
	 * The reset function will return the same value as the initial value.
	 *
	 * @param value the initial value.
	 */
	public Ref(T value) {
		this(value, () -> value);
	}

	/**
	 * Creates a new reference with the given value and reset function.
	 *
	 * @param value   the initial value.
	 * @param resetFn the reset function.
	 */
	public Ref(T value, @NonNull Supplier<T> resetFn) {
		this.value = value;
		this.resetFn = resetFn;
	}

	/**
	 * @return the current value.
	 */
	public T get() {
		return value;
	}

	/**
	 * Sets the value to the given value.
	 *
	 * @param value the new value.
	 */
	public void set(T value) {
		this.value = value;
	}

	/**
	 * Resets the value to its initial value.
	 */
	public void reset() {
		this.value = this.resetFn.get();
	}
}
