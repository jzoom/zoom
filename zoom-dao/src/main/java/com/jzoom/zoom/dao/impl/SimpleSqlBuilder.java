package com.jzoom.zoom.dao.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.jzoom.zoom.dao.Record;
import com.jzoom.zoom.dao.SqlBuilder;
import com.jzoom.zoom.dao.SqlDriver;

public class SimpleSqlBuilder implements SqlBuilder{
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
	

	public void clear() {
		having.setLength(0);
		sql.setLength(0);
		where.setLength(0);
		orderBy.setLength(0);
		join.setLength(0);
		groupBy.setLength(0);
		
		select.clear();
		values.clear();
	}
	
	@Override
	public SqlBuilder where(String name, Object value) {
		andWhere();
		where.append(name).append("=?");
		addValue(name,value);
		
		return this;
	}
	
	private void andWhere() {
		if (where.length()==0) {
			where.append(" WHERE ");
		} else {
			where.append(" AND ");
		}
	}

	@Override
	public SqlBuilder like(String name, Like like, String value) {
		assert(name!=null && like!=null);
		checkValue(value);
		andWhere();
		where.append(name).append(" LIKE ?");
		addValue(name,like.toValue(value));
		return this;
	}
	
	private void checkValue(Object value) {
		if(value == null) {
			throw new RuntimeException("值为null?请使用whereNull或者whereNotNull版本");
		}
	}

	@Override
	public SqlBuilder where(String name, Symbo symbo, String value) {
		assert(name!=null && symbo!=null);
		checkValue(value);
		
		if (where.length() == 0) {
			where.append(" WHERE ");
		} else {
			where.append(" AND ");
		}
		where.append(name).append(symbo.value()).append("?");
		addValue(name,value);
		
		return this;
	}

	@Override
	public SqlBuilder whereNull(String name) {
		andWhere();
		where.append(" IS NULL");
		return this;
	}
	
	@Override
	public SqlBuilder whereNotNull(String name) {
		andWhere();
		where.append(" NOT (").append(name).append(" IS NULL)");
		return this;
	}

	@Override
	public SqlBuilder whereIn(String name, Object... values) {
		andWhere();
		where.append(name).append(" IN (");
		boolean first = true;
		for (Object object : values) {
			if(first) {
				first = false;
			}else {
				where.append(",");
			}
			where.append("?");
			this.addValue(name,object);
		}
		where.append(')');
		
		return this;
	}

	private void addValue(String name, Object value) {
		
		this.values.add(value);
	}


	@Override
	public SqlBuilder where(Condition condition) {
		condition.where(this);
		return this;
	}

	@Override
	public SqlBuilder having(String name, Symbo symbo, Object value) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SqlBuilder orWhere(String name, Object value) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SqlBuilder orWhere(String name, Symbo symbo, Object value) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SqlBuilder whereCondition(String value, Object... values) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SqlBuilder orWhere(Condition condition) {
		// TODO Auto-generated method stub
		return null;
	}

	

	@Override
	public SqlBuilder whereNotIn(String name, Object... values) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SqlBuilder notLike(String name, Like like, Object value) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SqlBuilder innerJoin(String otherTable, String on) {
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
		assert(sort!=null && field != null);
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
	public SqlBuilder count(String field) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SqlBuilder avg(String field) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SqlBuilder select(String... fields) {
		sql.append("SELECT ");
		boolean first = true;
		for (String field : fields) {
			if(first) {
				first = false;
			}else {
				sql.append(',');
			}
			sql.append(field);
		}
		return this;
	}
	
	public void buildSelect() {
		if(sql.length() == 0) {
			sql.append("SELECT * ");
		}
			sql
			.append(" FROM ")
			.append(table)
			.append(join)
			.append(where)
			.append(groupBy)
			.append(having)
			.append(orderBy);
	}
	
	
	public List<Object> getValues(){
		return values;
	}

	
	protected StringBuilder buildSelect(StringBuilder sql,List<String> select) {
		if(select.size() == 0) {
			return sql.append("SELECT *");
		}
		
		//
		for (String part : select) {
			
		}
		
		return sql;
	}
	
	public void buildUpdate(Map<String, Object> record) {
		if(record!=null)setAll(record);
		DaoUtil.buildUpdate( sql,values, driver, table,  where,this.record );
	}
	public void buildUpdate() {
		DaoUtil.buildUpdate( sql,values, driver, table,  where,this.record );
	}


	public void buildInsert(Map<String, Object> record) {
		if(record!=null)setAll(record);
		DaoUtil.buildInsert(sql, values, driver, table, this.record);
	}

	public void buildDelete() {
		DaoUtil.buildDelete(sql,table,where);
	}

	@Override
	public SqlBuilder set(String name, Object value) {
		record.put(name, value);
		return this;
	}


	@Override
	public SqlBuilder setAll(Map<String, Object> data) {
		record.putAll(data);
		return this;
	}




}
