package com.jzoom.zoom.dao.alias.impl;

import java.util.Map;

import com.jzoom.zoom.dao.adapter.NameAdapter;
import com.jzoom.zoom.dao.alias.AliasPolicy;
import com.jzoom.zoom.dao.meta.TableMeta;

public class MapNameAdapter implements NameAdapter {
	
	private AliasPolicy aliasPolicy;
	private Map<String, String> map;
	private TableMeta table;
	
	public MapNameAdapter(AliasPolicy aliasPolicy,Map<String, String> map ,TableMeta table ) {
		this.aliasPolicy = aliasPolicy;
		this.map = map;
		this.table = table;
	}
	
	@Override
	public String getFieldName(String column) {
		return aliasPolicy.getAlias(column);
	}

	@Override
	public String getColumnName(String field) {
		String name = map.get(field);
		if(name==null) {
			throw new RuntimeException("找不到字段"+field);
		}
		return name;
	}

}
