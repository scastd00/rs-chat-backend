package rs.chat.domain.service;

import java.util.List;

public interface SimpleService<T> {
	T save(T t);

	T update(T t);

	void delete(T t);

	T findById(Long id);

	List<T> findAll();

	boolean exists(Long id);

	boolean exists(String name);
}
