package edworld.common.infra.repo;

import static edworld.common.infra.util.TextUtil.quoted;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.Callable;
import java.util.regex.Matcher;

import org.apache.commons.io.IOUtils;
import org.hibernate.SQLQuery;
import org.hibernate.jdbc.ReturningWork;

import edworld.common.infra.Config;
import edworld.common.infra.boundary.Nullable;
import edworld.common.infra.boundary.NullableBigDecimal;
import edworld.common.infra.boundary.NullableBoolean;
import edworld.common.infra.boundary.NullableByteSequence;
import edworld.common.infra.boundary.NullableCalendar;
import edworld.common.infra.boundary.NullableDouble;
import edworld.common.infra.boundary.NullableEnum;
import edworld.common.infra.boundary.NullableInteger;
import edworld.common.infra.boundary.NullableStringSequence;
import edworld.common.infra.boundary.NullableTimestampCalendar;
import edworld.common.infra.boundary.TimestampCalendar;
import edworld.common.repo.Criteria;
import edworld.common.repo.CriteriaSQL;

public abstract class AbstractPersistentRepository {
	protected PersistenceManager persistenceContext;

	protected AbstractPersistentRepository(PersistenceManager persistenceContext) {
		this.persistenceContext = persistenceContext;
	}

	protected SQLQuery createQuery(String resourceName, Criteria<?> criteria, Integer limit, String... orderBy) {
		return persistenceContext.getHibernateSession()
				.createSQLQuery(getSqlResource(resourceName, (CriteriaSQL) criteria, limit, orderBy));
	}

	protected int executeSQL(final String sql, final Object... params) {
		return persistenceContext.transactionContext(new Callable<Integer>() {
			public Integer call() throws Exception {
				return persistenceContext.getHibernateSession().doReturningWork(new ReturningWork<Integer>() {
					public Integer execute(Connection connection) throws SQLException {
						PreparedStatement statement = connection.prepareStatement(sql);
						try {
							setParameters(statement, params);
							return statement.executeUpdate();
						} finally {
							statement.close();
						}
					}
				});
			}
		});
	}

	protected int executeReturningSQL(final String sql, final Object... params) {
		return persistenceContext.transactionContext(new Callable<Integer>() {
			public Integer call() throws Exception {
				return persistenceContext.getHibernateSession().doReturningWork(new ReturningWork<Integer>() {
					public Integer execute(Connection connection) throws SQLException {
						Integer result;
						PreparedStatement statement = connection.prepareStatement(sql);
						try {
							setParameters(statement, params);
							ResultSet query = statement.executeQuery();
							try {
								query.next();
								result = query.getInt(1);
							} finally {
								query.close();
							}
						} finally {
							statement.close();
						}
						return result;
					}
				});
			}
		});
	}

	protected void setParameters(PreparedStatement statement, final Object... params) throws SQLException {
		int index = 1;
		for (Object param : params) {
			if (!(param instanceof Nullable))
				setParameter(statement, index, param);
			else if (((Nullable) param).isNull())
				statement.setNull(index, ((Nullable) param).sqlType());
			else
				setParameter(statement, index, ((Nullable) param).getValue());
			index++;
		}
	}

	private void setParameter(PreparedStatement statement, int index, Object value) throws SQLException {
		if (value instanceof Integer)
			statement.setInt(index, (Integer) value);
		else if (value instanceof Double)
			statement.setDouble(index, (Double) value);
		else if (value instanceof BigDecimal)
			statement.setBigDecimal(index, (BigDecimal) value);
		else if (value instanceof TimestampCalendar) {
			Calendar calendar = ((TimestampCalendar) value).getTimestamp();
			statement.setTimestamp(index, new java.sql.Timestamp(calendar.getTime().getTime()), calendar);
		} else if (value instanceof Calendar) {
			Calendar calendar = (Calendar) value;
			statement.setDate(index, new java.sql.Date(calendar.getTime().getTime()), calendar);
		} else if (value instanceof Boolean)
			statement.setBoolean(index, (Boolean) value);
		else
			statement.setString(index, (String) value);
	}

	protected String getSqlResource(String resourceName, CriteriaSQL criteria, Integer limit, String... orderBy) {
		String sql = sqlFromResource(resourceName);
		if (criteria != null) {
			String clauses = criteria.toSQL();
			if (!clauses.isEmpty())
				sql = sql.replaceAll("--(WHERE|AND)", "$1 " + Matcher.quoteReplacement(clauses));
		}
		if (orderBy.length > 0) {
			String expressionOrderBy = "ORDER BY ";
			String prefix = "";
			for (String field : orderBy) {
				expressionOrderBy += prefix + field;
				prefix = ", ";
			}
			sql = sql.replaceAll("--ORDER BY", Matcher.quoteReplacement(expressionOrderBy));
		}
		if (limit != null)
			sql += " LIMIT " + limit;
		return sql;
	}

	private String sqlFromResource(String resourceName) {
		InputStream input = getClass().getResourceAsStream("/sql/" + resourceName);
		if (input == null)
			return "SELECT *\nFROM " + resourceName.replace(".sql", "") + "\n--WHERE\n--ORDER BY\n";
		try {
			try {
				return IOUtils.toString(input, Config.getEncoding());
			} finally {
				input.close();
			}
		} catch (IOException e) {
			throw new IllegalArgumentException("Service unavailable. Cause: " + cause(e));
		}
	}

	protected String cause(IOException e) {
		if (e.getCause() == null)
			return e.getMessage();
		return e.getCause().getMessage();
	}

	protected Integer intValue(Object value) {
		return value == null ? null : ((BigDecimal) value).intValue();
	}

	protected Long longValue(Object value) {
		return value == null ? null : ((BigDecimal) value).longValue();
	}

	protected String ds(String value) {
		return value;
	}

	protected NullableInteger ds(Integer value) {
		return new NullableInteger(value);
	}

	protected NullableBigDecimal ds(BigDecimal value) {
		return new NullableBigDecimal(value);
	}

	protected NullableDouble ds(Double value) {
		return new NullableDouble(value);
	}

	protected NullableCalendar ds(Calendar value) {
		return new NullableCalendar(value);
	}

	protected NullableTimestampCalendar ds(TimestampCalendar value) {
		return new NullableTimestampCalendar(value);
	}

	protected NullableBoolean ds(Boolean value) {
		return new NullableBoolean(value);
	}

	protected NullableEnum ds(Enum<?> value) {
		return new NullableEnum(value);
	}

	protected NullableStringSequence ds(String[] stringSequence) {
		return new NullableStringSequence(stringSequence);
	}

	protected NullableByteSequence ds(byte[] byteSequence) {
		return new NullableByteSequence(byteSequence);
	}

	protected String stringFromClob(Object value) {
		try {
			return value == null ? null : ((Clob) value).getSubString(1, (int) ((Clob) value).length());
		} catch (SQLException e) {
			throw new IllegalArgumentException(e);
		}
	}

	protected Calendar toCalendar(Object date) {
		if (date == null)
			return null;
		Calendar calendar = Calendar.getInstance();
		calendar.setTime((Date) date);
		return calendar;
	}

	protected TimestampCalendar toTimestamp(Object date) {
		if (date == null)
			return null;
		Calendar calendar = Calendar.getInstance();
		calendar.setTime((Date) date);
		return new TimestampCalendar(calendar);
	}

	protected void verifyIntegrity(String attribute, Object actualValue, Object expectedValue) {
		if (!expectedValue.equals(actualValue))
			throw new IllegalStateException(
					"Internal integrity error: " + quoted(attribute) + " should be " + quoted(expectedValue.toString())
							+ " instead of " + (actualValue == null ? "NULL" : quoted(actualValue.toString())) + ".");
	}
}
