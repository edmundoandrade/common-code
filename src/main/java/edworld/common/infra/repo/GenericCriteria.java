package edworld.common.infra.repo;

import java.util.Map;

import edworld.common.infra.util.TextUtil;
import edworld.common.repo.Criteria;
import edworld.common.repo.CriteriaSQL;

public class GenericCriteria implements Criteria<Map<String, Object>>, CriteriaSQL {
	private Map<String, Object> map;

	public GenericCriteria(Map<String, Object> map) {
		this.map = map;
	}

	public boolean satisfy(Map<String, Object> item) {
		for (String fieldName : map.keySet())
			if (!item.get(fieldName).equals(map.get(fieldName)))
				return false;
		return true;
	}

	@Override
	public String toSQL() {
		String result = "";
		String delim = "";
		for (String fieldName : map.keySet()) {
			result += delim + fieldName + " = " + TextUtil.quoted(map.get(fieldName).toString());
			delim = " AND ";
		}
		return result;
	}
}
