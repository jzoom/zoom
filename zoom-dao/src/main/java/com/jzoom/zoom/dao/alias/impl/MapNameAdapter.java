package com.jzoom.zoom.dao.alias.impl;

import java.util.Map;

import com.jzoom.zoom.dao.adapter.NameAdapter;
import com.jzoom.zoom.dao.alias.AliasPolicy;

public class MapNameAdapter implements NameAdapter {
	
	private AliasPolicy aliasPolicy;
	private Map<String, String> map;
	
	
	public MapNameAdapter(AliasPolicy aliasPolicy,Map<String, String> map) {
		this.aliasPolicy = aliasPolicy;
		this.map = map;
	}
	
	@Override
	public String getFieldName(String column) {
		return aliasPolicy.getAlias(column);
	}

	@Override
	public String getColumnName(String field) {
		return map.get(field);
	}

}
