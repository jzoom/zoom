package com.jzoom.zoom.dao.impl;

import java.util.Map;
import java.util.Map.Entry;

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

	
}
