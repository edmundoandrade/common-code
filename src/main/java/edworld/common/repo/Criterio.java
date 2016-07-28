package edworld.common.repo;

public interface Criterio<T> {
	boolean satisfaz(T item);
}
