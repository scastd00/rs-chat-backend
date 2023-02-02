package rs.chat.utils.ref;

import lombok.NonNull;

import java.util.function.Supplier;

public final class Ref<T> {
	private T value;
	private final Supplier<T> resetFn;

	public Ref(T value) {
		this(value, () -> value);
	}

	public Ref(T value, @NonNull Supplier<T> resetFn) {
		this.value = value;
		this.resetFn = resetFn;
	}

	public T get() {
		return value;
	}

	public void set(T value) {
		this.value = value;
	}

	public void reset() {
		this.value = this.resetFn.get();
	}
}
