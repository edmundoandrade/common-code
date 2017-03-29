package edworld.common.infra.repo;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.hibernate.SQLQuery;
import org.hibernate.transform.AliasToEntityMapResultTransformer;

import edworld.common.repo.Criteria;
import edworld.common.repo.Repository;
import edworld.common.repo.RepositoryException;

public class GenericPersistentRepository extends AbstractPersistentRepository
		implements Repository<Map<String, Object>> {
	protected String entityType;

	public GenericPersistentRepository(String entityType, PersistenceManager persistenceContext) {
		super(persistenceContext);
		this.entityType = entityType;
	}

	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> list(Principal principal, Criteria<Map<String, Object>> criterio, Integer limite,
			String... orderBy) {
		SQLQuery query = createQuery("Orgao.sql", criterio, limite, orderBy);
		query.setResultTransformer(AliasToEntityMapResultTransformer.INSTANCE);
		return (List<Map<String, Object>>) query.list();
	}

	public void insert(Map<String, Object> item, Principal principal) {
		String fields = "";
		String values = "";
		String prefix = "";
		boolean genetaredPK = false;
		List<Object> parms = new ArrayList<>();
		for (String field : item.keySet()) {
			fields += prefix + field;
			if (field.equalsIgnoreCase("id")) {
				values += prefix + "DEFAULT";
				genetaredPK = true;
			} else if (field.equalsIgnoreCase("userId")) {
				values += prefix + "?";
				parms.add(ds(principal.getName()));
			} else if (field.equalsIgnoreCase("timestamp"))
				values += prefix + "current_timestamp";
			else if (field.equalsIgnoreCase("version"))
				values += prefix + "1";
			else {
				values += prefix + "?";
				parms.add(dsObject(item.get(field)));
			}
			prefix = ", ";
		}
		String sql = "INSERT INTO " + entityType + " (" + fields + ") VALUES (" + values + ")";
		if (genetaredPK) {
			sql += " RETURNING id";
			int id = executeReturningSQL(sql, parms.toArray());
			item.put("id", id);
		} else
			executeSQL(sql, parms.toArray());
		incrementVersion(item);
	}

	public void update(Map<String, Object> item, Principal principal) throws RepositoryException {
		String filter = "";
		String values = "";
		String prefix = "";
		String prefixFilter = "";
		List<Object> parms = new ArrayList<>();
		List<Object> parmsFilter = new ArrayList<>();
		for (String field : item.keySet())
			if (field.equalsIgnoreCase("id")) {
				filter += prefixFilter + "=?";
				prefixFilter = " AND ";
				parmsFilter.add(dsObject(item.get(field)));
			} else {
				if (field.equalsIgnoreCase("userId")) {
					values += prefix + field + "=?";
					parms.add(ds(principal.getName()));
				} else if (field.equalsIgnoreCase("timestamp"))
					values += prefix + field + "=current_timestamp";
				else if (field.equalsIgnoreCase("version")) {
					values += prefix + field + "=" + field + "+1";
					filter += prefixFilter + "=?";
					prefixFilter = " AND ";
					parmsFilter.add(dsObject(item.get(field)));
				} else {
					values += prefix + field + "=?";
					parms.add(dsObject(item.get(field)));
				}
				prefix = ", ";
			}
		parms.addAll(parmsFilter);
		String sql = "UPDATE " + entityType + " SET " + values + " WHERE " + filter;
		int qtdLinhas = executeSQL(sql, parms);
		if (qtdLinhas == 0)
			throw new RepositoryException(entityType);
		incrementVersion(item);
	}

	public void delete(Map<String, Object> item, Principal principal) {
		String filter = "";
		String prefixFilter = "";
		List<Object> parmsFilter = new ArrayList<>();
		for (String field : item.keySet())
			if (field.equalsIgnoreCase("id")) {
				filter += prefixFilter + "=?";
				prefixFilter = " AND ";
				parmsFilter.add(dsObject(item.get(field)));
			}
		executeSQL("DELETE FROM " + entityType + " WHERE " + filter, parmsFilter);
	}

	protected void incrementVersion(Map<String, Object> item) {
		Object version = item.containsKey("version") ? (Integer) item.get("version") + 1 : 1;
		item.put("version", version);
	}
}
