package edworld.common.infra.repo;

import static org.junit.Assert.assertEquals;

import java.security.Principal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import edworld.common.infra.util.JSONUtil;

public class GenericPersistentRepositoryTest {
	private GenericPersistentRepository repository;
	private Principal principal;
	private List<String> output = new ArrayList<>();

	@Before
	public void setup() {
		repository = new GenericPersistentRepository("Table", new PersistenceManager(null)) {
			@Override
			protected int executeSQL(String sql, Object... params) {
				output.add(sql);
				output.add(JSONUtil.toString(params));
				return 1;
			}

			@Override
			protected int executeReturningSQL(String sql, Object... params) {
				return executeSQL(sql, params);
			}
		};
		principal = new Principal() {
			public String getName() {
				return "user@test.test";
			}
		};
	}

	@Test
	public void insert() {
		Map<String, Object> item = new HashMap<>();
		item.put("fieldA", "valueA");
		repository.insert(item, principal);
		assertEquals(2, output.size());
		assertEquals("INSERT INTO Table (fieldA) VALUES (?)", output.get(0));
		assertEquals("[ \"valueA\" ]", output.get(1));
		output.clear();
		item.put("id", null);
		repository.insert(item, principal);
		assertEquals(2, output.size());
		assertEquals("INSERT INTO Table (fieldA, id, version) VALUES (?, DEFAULT, 1) RETURNING id", output.get(0));
		assertEquals("[ \"valueA\" ]", output.get(1));
	}
}
