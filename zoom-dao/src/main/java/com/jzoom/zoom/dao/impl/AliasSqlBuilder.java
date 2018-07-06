package com.jzoom.zoom.dao.impl;

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
	public void clear() {
		nameAdapter = null;
		super.clear();
	}
	
	
	@Override
	public SqlBuilder table(String table) {
		nameAdapter = aliasPolicyManager.getPolicy(table);
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

}
