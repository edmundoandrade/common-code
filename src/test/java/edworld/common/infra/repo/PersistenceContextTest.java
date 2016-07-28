package edworld.common.infra.repo;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class PersistenceContextTest {
	@Test
	public void nestedTransactions() {
		PersistenceManager repositorio = new PersistenceManager(null);
		assertEquals(0, repositorio.getPendingTransactions());
		repositorio.beginTransaction();
		assertEquals(1, repositorio.getPendingTransactions());
		repositorio.beginTransaction();
		assertEquals(2, repositorio.getPendingTransactions());
		repositorio.beginTransaction();
		assertEquals(3, repositorio.getPendingTransactions());
		repositorio.commitTransaction();
		assertEquals(2, repositorio.getPendingTransactions());
		repositorio.rollbackTransaction();
		assertEquals(1, repositorio.getPendingTransactions());
		repositorio.rollbackTransaction();
		assertEquals(0, repositorio.getPendingTransactions());
	}
}
