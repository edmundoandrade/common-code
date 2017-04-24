package edworld.common.infra.repo;

import static org.junit.Assert.assertEquals;

import java.security.Principal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import edworld.common.infra.boundary.Nullable;
import edworld.common.infra.util.JSONUtil;
import edworld.common.repo.Criteria;
import edworld.common.repo.CriteriaSQL;
import edworld.common.repo.RepositoryException;

public class GenericPersistentRepositoryTest {
	private GenericPersistentRepository repository;
	private Principal principal;
	private List<String> output = new ArrayList<>();

	@Before
	public void setup() {
		repository = new GenericPersistentRepository("Table", "site, numero", "userId", "timestamp", "version",
				new PersistenceManager(null), "numero",
				"(SELECT COALESCE(MAX(numero),0) FROM Table WHERE site=?) + 1") {
			@Override
			public List<Map<String, Object>> list(Principal principal, Criteria<Map<String, Object>> criteria,
					Integer limit, String... orderBy) {
				output.add(getSqlResource(entityType + ".sql", (CriteriaSQL) criteria, limit, orderBy));
				return new ArrayList<>();
			}

			@Override
			protected int executeSQL(String sql, Object... params) {
				output.add(sql);
				output.add(toJSON(params));
				return 1;
			}

			@Override
			protected int executeReturningSQL(String sql, Object... params) {
				return executeSQL(sql, params);
			}

			private String toJSON(Object param) {
				if (param instanceof Nullable)
					return JSONUtil.toString(((Nullable) param).getValue());
				if (param instanceof Object[]) {
					String jsonParams = "[";
					String jsonParamsDelim = " ";
					for (Object item : ((Object[]) param)) {
						jsonParams += jsonParamsDelim + toJSON(item);
						jsonParamsDelim = ", ";
					}
					jsonParams += " ]";
					return jsonParams;
				}
				return JSONUtil.toString(param);
			}
		};
		principal = new Principal() {
			public String getName() {
				return "user@test.test";
			}
		};
	}

	@Test
	public void list() {
		repository.list(principal, null, null, "numero");
		assertEquals(1, output.size());
		assertEquals("SELECT *\nFROM Table\n--WHERE\nORDER BY numero\n", output.get(0));
	}

	@Test
	public void insert() {
		Map<String, Object> item = new HashMap<>();
		item.put("site", 1);
		item.put("fieldA", "valueA");
		repository.insert(item, principal);
		assertEquals(2, output.size());
		assertEquals(
				"INSERT INTO Table (site, fieldA, numero, userId, timestamp, version) VALUES (?, ?, (SELECT COALESCE(MAX(numero),0) FROM Table WHERE site=?) + 1, ?, current_timestamp, 1) RETURNING numero",
				output.get(0));
		assertEquals("[ 1, \"valueA\", 1, \"user@test.test\" ]", output.get(1));
	}

	@Test
	public void update() throws RepositoryException {
		Map<String, Object> item = new HashMap<>();
		item.put("site", 1);
		item.put("numero", 42);
		item.put("fieldA", "valueA");
		item.put("version", 1);
		repository.update(item, principal);
		assertEquals(2, output.size());
		assertEquals(
				"UPDATE Table SET fieldA=?, version=version+1, userId=?, timestamp=current_timestamp WHERE site=? AND numero=? AND version=?",
				output.get(0));
		assertEquals("[ \"valueA\", \"user@test.test\", 1, 42, 1 ]", output.get(1));
	}

	@Test
	public void delete() {
		Map<String, Object> item = new HashMap<>();
		item.put("site", 1);
		item.put("numero", 42);
		item.put("fieldA", "valueA");
		repository.delete(item, principal);
		assertEquals(2, output.size());
		assertEquals("DELETE FROM Table WHERE site=? AND numero=?", output.get(0));
		assertEquals("[ 1, 42 ]", output.get(1));
		output.clear();
	}
}
