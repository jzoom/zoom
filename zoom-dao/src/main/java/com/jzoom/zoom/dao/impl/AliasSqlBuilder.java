package com.jzoom.zoom.dao.impl;

import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;

import com.jzoom.zoom.dao.DaoException;
import com.jzoom.zoom.dao.SqlBuilder;
import com.jzoom.zoom.dao.adapter.NameAdapter;
import com.jzoom.zoom.dao.alias.AliasPolicyManager;
import com.jzoom.zoom.dao.driver.SqlDriver;

public class AliasSqlBuilder extends SimpleSqlBuilder {
	private AliasPolicyManager aliasPolicyManager;
	NameAdapter nameAdapter;
	public AliasSqlBuilder(SqlDriver driver, AliasPolicyManager aliasPolicyManager) {
		super(driver);
		this.aliasPolicyManager = aliasPolicyManager;
	}
	
	@Override
	public void clear(boolean all) {
		if(all) {
			nameAdapter = null;
		}
		
		super.clear(all);
	}
	
	
	@Override
	public SqlBuilder table(String table) {
		nameAdapter = aliasPolicyManager.getNameAdapter(table);
		return super.table(table);
	}
	
	@Override
	public SqlBuilder orderBy(String field, Sort sort) {
		field = nameAdapter.getColumnName(field);
		return super.orderBy(field, sort);
	}
	
	@Override
	protected SqlBuilder whereImpl(String name, Symbo symbo, Object value, String relation) {
		name = nameAdapter.getColumnName(name);
		return super.whereImpl(name, symbo, value, relation);
	}
	
	@Override
	public SqlBuilder setAll(Map<String, Object> data) {
		for (Entry<String, Object> entry : data.entrySet()) {
			String key = entry.getKey();
			String name = nameAdapter.getColumnName(key);
			super.set(name, entry.getValue());
		}
		
		return this;
		
		
		//return super.setAll(data);
	}
	
	@Override
	public SqlBuilder set(String name, Object value) {
		name = nameAdapter.getColumnName(name);
		return super.set(name, value);
	}
	
	protected String getName(String column) {
		if( column.contains("(")) {
			throw new DaoException("不支持函数，如要使用函数，请使用selctX版本的方法,或使用selectRaw");
		}
		String name = nameAdapter.getColumnName(column);
		return name;
	}
	
	
	@Override
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
				driver.protectColumn(sql,getName(matcher.group(1)));
				sql.append(" AS ");
				driver.protectColumn(sql, matcher.group(2));
			} else {
				driver.protectColumn(sql, getName(part));

			}
		}
	}
	
	
	
}
