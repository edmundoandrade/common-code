package edworld.common.repo;

public interface Criteria<T> {
	boolean satisfy(T item);
}
