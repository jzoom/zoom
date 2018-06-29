package com.jzoom.zoom.dao.impl;

import com.jzoom.zoom.dao.Entity;
import com.jzoom.zoom.dao.adapter.EntityAdapter;

public class DefaultEntity implements Entity {
	
	private String tableName;
	private Class<?> type;

	@Override
	public String getTableName() {
		return tableName;
	}

	@Override
	public Class<?> getType() {
		return type;
	}

	@Override
	public EntityAdapter[] getAdapters() {
		return null;
	}

}
