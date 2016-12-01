package edworld.common.repo;

import edworld.common.infra.repo.PersistenceManager;

public interface PersistenceWork {
	void execute(PersistenceManager persistenceContext) throws RepositoryException;
}
