package edworld.common.infra.repo;

import static edworld.common.infra.util.RegexUtil.regexHTML;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.hibernate.SQLQuery;
import org.hibernate.transform.AliasToEntityMapResultTransformer;

import edworld.common.infra.util.RegexUtil;
import edworld.common.repo.Criteria;
import edworld.common.repo.Repository;
import edworld.common.repo.RepositoryException;

public class GenericPersistentRepository extends AbstractPersistentRepository
		implements Repository<Map<String, Object>> {
	protected String entityType;
	protected String generatedPK;
	protected String userIdField;
	protected String timestampField;
	protected String versionField;
	protected String generatedPKFormula;

	public GenericPersistentRepository(String entityType, String generatedPK, String userIdField, String timestampField,
			String versionField, PersistenceManager persistenceContext) {
		this(entityType, generatedPK, userIdField, timestampField, versionField, persistenceContext, "DEFAULT");
	}

	public GenericPersistentRepository(String entityType, String generatedPK, String userIdField, String timestampField,
			String versionField, PersistenceManager persistenceContext, String generatedPKFormula) {
		super(persistenceContext);
		this.entityType = entityType;
		this.generatedPK = generatedPK;
		this.userIdField = userIdField;
		this.timestampField = timestampField;
		this.versionField = versionField;
		this.generatedPKFormula = generatedPKFormula;
	}

	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> list(Principal principal, Criteria<Map<String, Object>> criteria, Integer limit,
			String... orderBy) {
		SQLQuery query = createQuery(entityType + ".sql", criteria, limit, orderBy);
		query.setResultTransformer(AliasToEntityMapResultTransformer.INSTANCE);
		return (List<Map<String, Object>>) query.list();
	}

	public void insert(Map<String, Object> item, Principal principal) {
		String fields = "";
		String values = "";
		String prefix = "";
		List<Object> parms = new ArrayList<>();
		for (String field : getFieldNames(item)) {
			fields += prefix + field;
			if (field.equalsIgnoreCase(generatedPK)) {
				values += prefix + generatedPKFormula;
				for (String fieldName : RegexUtil.listOccurrences(regexHTML("(\\w+)\\s*=\\s*\\?"), generatedPKFormula))
					parms.add(dsObject(item.get(fieldName)));
			} else if (field.equalsIgnoreCase(userIdField)) {
				values += prefix + "?";
				parms.add(ds(principal.getName()));
			} else if (field.equalsIgnoreCase(timestampField))
				values += prefix + "current_timestamp";
			else if (field.equalsIgnoreCase(versionField))
				values += prefix + "1";
			else {
				values += prefix + "?";
				parms.add(dsObject(item.get(field)));
			}
			prefix = ", ";
		}
		String sql = "INSERT INTO " + entityType + " (" + fields + ") VALUES (" + values + ")";
		if (generatedPK != null) {
			sql += " RETURNING " + generatedPK;
			int id = executeReturningSQL(sql, parms.toArray());
			item.put(generatedPK, id);
		} else
			executeSQL(sql, parms.toArray());
		if (versionField != null)
			incrementVersion(item);
	}

	public void update(Map<String, Object> item, Principal principal) throws RepositoryException {
		String filter = "";
		String values = "";
		String prefix = "";
		String prefixFilter = "";
		List<Object> parms = new ArrayList<>();
		List<Object> parmsFilter = new ArrayList<>();
		for (String field : getFieldNames(item))
			if (field.equalsIgnoreCase(generatedPK)) {
				filter += prefixFilter + field + "=?";
				prefixFilter = " AND ";
				parmsFilter.add(dsObject(item.get(field)));
			} else {
				if (field.equalsIgnoreCase(userIdField)) {
					values += prefix + field + "=?";
					parms.add(ds(principal.getName()));
				} else if (field.equalsIgnoreCase(timestampField))
					values += prefix + field + "=current_timestamp";
				else if (field.equalsIgnoreCase(versionField)) {
					values += prefix + field + "=" + field + "+1";
					filter += prefixFilter + field + "=?";
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
		int qtdLinhas = executeSQL(sql, parms.toArray());
		if (qtdLinhas == 0)
			throw new RepositoryException(entityType);
		incrementVersion(item);
	}

	public void delete(Map<String, Object> item, Principal principal) {
		String filter = "";
		String prefixFilter = "";
		List<Object> parmsFilter = new ArrayList<>();
		for (String field : getFieldNames(item))
			if (field.equalsIgnoreCase(generatedPK)) {
				filter += prefixFilter + field + "=?";
				prefixFilter = " AND ";
				parmsFilter.add(dsObject(item.get(field)));
			}
		executeSQL("DELETE FROM " + entityType + " WHERE " + filter, parmsFilter.toArray());
	}

	protected void incrementVersion(Map<String, Object> item) {
		Object version = item.containsKey(versionField) ? (Integer) item.get(versionField) + 1 : 1;
		item.put(versionField, version);
	}

	protected List<String> getFieldNames(Map<String, Object> map) {
		List<String> fieldNames = new ArrayList<>(map.keySet());
		if (generatedPK != null && !fieldNames.contains(generatedPK))
			fieldNames.add(generatedPK);
		if (userIdField != null && !fieldNames.contains(userIdField))
			fieldNames.add(userIdField);
		if (timestampField != null && !fieldNames.contains(timestampField))
			fieldNames.add(timestampField);
		if (versionField != null && !fieldNames.contains(versionField))
			fieldNames.add(versionField);
		return fieldNames;
	}
}
