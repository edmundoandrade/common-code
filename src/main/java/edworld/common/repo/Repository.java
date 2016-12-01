package edworld.common.repo;

import java.security.Principal;
import java.util.List;

public interface Repository<T> {
	List<T> list(Principal principal, Criteria<T> criteria, Integer limit, String... orderBy);

	void insert(T item, Principal principal);

	void update(T item, Principal principal) throws RepositoryException;

	void delete(T item, Principal principal);
}
