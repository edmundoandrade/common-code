package edworld.common.infra.repo;

import java.util.concurrent.Callable;

import javax.persistence.EntityManager;

import org.hibernate.Session;

public class PersistenceManager {
	private EntityManager entityManager;
	private boolean transactionManagedByContainer;
	private int pendingTransactions;

	public PersistenceManager(EntityManager entityManager) {
		this.entityManager = entityManager;
		try {
			if (entityManager != null)
				entityManager.getTransaction();
			transactionManagedByContainer = false;
		} catch (IllegalStateException e) {
			transactionManagedByContainer = true;
		}
	}

	protected Session getHibernateSession() {
		return entityManager.unwrap(Session.class);
	}

	public int getPendingTransactions() {
		return pendingTransactions;
	}

	public void transactionContext(Runnable work) {
		beginTransaction();
		try {
			work.run();
		} catch (Throwable e) {
			rollbackTransaction();
			throw e;
		}
		commitTransaction();
	}

	public <V> V transactionContext(Callable<V> work) {
		V result;
		beginTransaction();
		try {
			result = work.call();
		} catch (Exception e) {
			rollbackTransaction();
			throw new IllegalArgumentException(e);
		}
		commitTransaction();
		return result;
	}

	protected void beginTransaction() {
		pendingTransactions++;
		if (pendingTransactions != 1)
			return;
		if (entityManager != null && !transactionManagedByContainer)
			entityManager.getTransaction().begin();
	}

	protected void rollbackTransaction() {
		pendingTransactions--;
		if (pendingTransactions != 0)
			return;
		if (entityManager != null && !transactionManagedByContainer)
			entityManager.getTransaction().rollback();
	}

	protected void commitTransaction() {
		pendingTransactions--;
		if (pendingTransactions != 0)
			return;
		if (entityManager != null && !transactionManagedByContainer)
			entityManager.getTransaction().commit();
	}
}
