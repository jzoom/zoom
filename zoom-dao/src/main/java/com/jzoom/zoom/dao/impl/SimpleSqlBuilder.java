package com.jzoom.zoom.dao.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.jzoom.zoom.dao.Record;
import com.jzoom.zoom.dao.SqlBuilder;
import com.jzoom.zoom.dao.driver.SqlDriver;

public class SimpleSqlBuilder implements SqlBuilder {
	protected static final char SPACE = ' ';

	protected StringBuilder sql;
	protected StringBuilder where;
	protected StringBuilder orderBy;
	protected StringBuilder groupBy;
	protected StringBuilder join;

	protected List<String> select;

	protected List<Object> values;

	protected String table;
	protected StringBuilder having;
	protected SqlDriver driver;

	private Record record;

	public SimpleSqlBuilder(SqlDriver driver) {
		this.driver = driver;

		sql = new StringBuilder();
		where = new StringBuilder();
		orderBy = new StringBuilder();
		join = new StringBuilder();
		groupBy = new StringBuilder();
		select = new ArrayList<String>();
		having = new StringBuilder();
		record = new Record();
		values = new ArrayList<Object>();
	}

	public void clear( boolean all ) {
		if(all) {
			where.setLength(0);
			join.setLength(0);
			groupBy.setLength(0);
			values.clear();
			having.setLength(0);
		}
		
		sql.setLength(0);
		orderBy.setLength(0);
		record.clear();
		select.clear();
	}

	private void andWhere() {
		if (where.length() == 0) {
			where.append(" WHERE ");
		} else {
			where.append(" AND ");
		}
	}

	@Override
	public SqlBuilder like(String key, Like like, Object value) {
		assert (key != null && like != null);
		checkValue(value);
		andWhere();
		where.append(key).append(" LIKE ?");
		addValue(key, like.toValue(value));
		return this;
	}

	private void checkValue(Object value) {
		if (value == null) {
			throw new RuntimeException("值为null?请使用whereNull或者whereNotNull版本");
		}
	}

	@Override
	public SqlBuilder where(String key, Symbo symbo, Object value) {
		assert (key != null && symbo != null);

		return whereImpl(key, symbo, value, " AND ");

	}

	@Override
	public SqlBuilder where(String key, Object value) {
		return whereImpl(key, Symbo.EQ, value, " AND ");
	}

	protected SqlBuilder whereImpl(String key, Symbo symbo, Object value, String relation) {
		checkValue(value);

		if (where.length() == 0) {
			where.append(" WHERE ");
		} else {
			where.append(relation);
		}
		where.append(key).append(symbo.value()).append("?");
		addValue(key, value);
		return this;
	}

	@Override
	public SqlBuilder orWhere(String key, Object value) {

		return orWhere(key, Symbo.EQ, value);
	}

	@Override
	public SqlBuilder orWhere(String key, Symbo symbo, Object value) {
		return whereImpl(key, symbo, value, " OR ");
	}

	@Override
	public SqlBuilder whereNull(String key) {
		andWhere();
		where.append(key).append(" IS NULL");
		return this;
	}

	@Override
	public SqlBuilder whereNotNull(String key) {
		andWhere();
		where.append(" NOT (").append(key).append(" IS NULL)");
		return this;
	}

	@Override
	public SqlBuilder whereIn(String key, Object... values) {
		andWhere();
		where.append(key).append(" IN (");
		boolean first = true;
		for (Object object : values) {
			if (first) {
				first = false;
			} else {
				where.append(",");
			}
			where.append("?");
			this.addValue(key, object);
		}
		where.append(')');

		return this;
	}

	@Override
	public SqlBuilder innerJoin(String otherTable, String on) {

		return join(otherTable, on, "INNER");
	}

	public SqlBuilder join(String table, String on, String type) {
		join.append(SPACE).append(type).append(" JOIN ").append(table).append(" ON ").append(on);
		return this;
	}

	private void addValue(String key, Object value) {

		this.values.add(value);
	}

	@Override
	public SqlBuilder where(Condition condition) {
		condition.where(this);
		return this;
	}

	@Override
	public SqlBuilder having(String key, Symbo symbo, Object value) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SqlBuilder whereCondition(String key, Object... values) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SqlBuilder orWhere(Condition condition) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SqlBuilder whereNotIn(String key, Object... values) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SqlBuilder notLike(String key, Like like, Object value) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SqlBuilder union(SqlBuilder builder) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SqlBuilder unionAll(SqlBuilder builder) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SqlBuilder orderBy(String field, Sort sort) {
		assert (sort != null && field != null);
		if (orderBy.length() == 0) {
			orderBy.append(" ORDER BY ");
		} else {
			orderBy.append(',');
		}
		orderBy.append(field).append(SPACE).append(sort.value());
		return this;
	}

	@Override
	public SqlBuilder groupBy(String group) {
		groupBy.append(" GROUP BY ").append(group);
		return this;
	}

	@Override
	public SqlBuilder table(String table) {
		this.table = table;
		return this;
	}

	@Override
	public SqlBuilder sum(String field) {
		// TODO Auto-generated method stub
		return null;
	}

	public void buildLimit(int position, int pageSize) {
		buildSelect();
		driver.buildPage(sql, position, pageSize);
	}
	
	public int getPageFromPosition(int position,int pageSize) {
		return driver.position2page(position,pageSize);
	}
	
	
	@Override
	public SqlBuilder max(String field) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SqlBuilder min(String field) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SqlBuilder count() {
		selectRaw("COUNT(*) AS COUNT");
		return this;
	}

	@Override
	public SqlBuilder avg(String field) {
		// TODO Auto-generated method stub
		return null;
	}
	
	public SqlBuilder selectRaw(String fields) {
		if(sql.length() == 0) {
			sql.append("SELECT ");
		}
		sql.append(fields);
		return this;
	}

	@Override
	public SqlBuilder select(String... fields) {
		sql.append("SELECT ");
		boolean first = true;
		for (String field : fields) {
			if (first) {
				first = false;
			} else {
				sql.append(',');
			}
			parseSelect(sql, field);
		}
		return this;
	}

	private static final Log log = LogFactory.getLog(SimpleSqlBuilder.class);

	/**
	 * select 中的形式有 函数(字段,字段) as 字段 , 字段 as 字段,
	 * 
	 * @param sql
	 * @param select
	 */
	protected void parseSelect(StringBuilder sql, String select) {
		if ("*".equals(select)) {
			sql.append("*");
			return;
		}
		String[] parts = select.split(",");
		Matcher matcher = null;
		boolean first = true;
		for (String part : parts) {
			if (first) {
				first = false;
			} else {
				sql.append(",");
			}
			if ((matcher = BuilderKit.AS_PATTERN.matcher(part)).matches()) {
				driver.protectColumn(sql,matcher.group(1));
				sql.append(" AS ");
				driver.protectColumn(sql, matcher.group(2));
			} else {
				if (part.contains("(")) {
					sql.append(part);
				} else {
					driver.protectColumn(sql, part);
				}

			}
		}
	}

	public void buildSelect() {
		if (sql.length() == 0) {
			sql.append("SELECT * ");
		}
		sql.append(" FROM ");
		driver.protectTable(sql, table);
		sql.append(join)
			.append(where)
			.append(groupBy)
			.append(having)
			.append(orderBy);
	}

	public List<Object> getValues() {
		return values;
	}

	

	public void buildUpdate(Map<String, Object> record) {
		if (record != null)
			setAll(record);
		BuilderKit.buildUpdate(sql, values, driver, table, where, this.record);
	}

	public void buildUpdate() {
		BuilderKit.buildUpdate(sql, values, driver, table, where, this.record);
	}

	public void buildInsert() {
		BuilderKit.buildInsert(sql, values, driver, table, this.record);
	}

	public void buildDelete() {
		BuilderKit.buildDelete(sql, table, where);
	}

	@Override
	public SqlBuilder set(String key, Object value) {
		record.put(key, value);
		return this;
	}

	@Override
	public SqlBuilder setAll(Map<String, Object> data) {
		record.putAll(data);
		return this;
	}

}
