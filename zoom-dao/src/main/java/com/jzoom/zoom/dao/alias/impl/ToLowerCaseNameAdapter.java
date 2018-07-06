package com.jzoom.zoom.dao.alias.impl;

import com.jzoom.zoom.dao.adapter.NameAdapter;

public class ToLowerCaseNameAdapter implements NameAdapter {

	@Override
	public String getFieldName(String column) {
		return column.toLowerCase();
	}

	@Override
	public String getColumnName(String field) {
		return field.toUpperCase();
	}

}
