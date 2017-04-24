package edworld.common.infra.repo;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

import edworld.common.infra.util.JSONUtil;
import edworld.common.repo.Criteria;
import edworld.common.repo.Repository;

public class MemoryRepository<T> implements Repository<T> {
	public List<T> list(Principal principal, Criteria<T> criteria, Integer limit, String... orderBy) {
		return new ArrayList<>();
	}

	public void insert(T item, Principal principal) {
		System.out.println("MUDANCA.INSERT:" + JSONUtil.toString(item));
	}

	public void update(T item, Principal principal) {
		System.out.println("MUDANCA.UPDATE:" + JSONUtil.toString(item));
	}

	public void delete(T item, Principal principal) {
		System.out.println("MUDANCA.DELETE:" + JSONUtil.toString(item));
	}
}
