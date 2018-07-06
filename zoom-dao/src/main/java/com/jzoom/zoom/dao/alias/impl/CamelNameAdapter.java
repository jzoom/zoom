package com.jzoom.zoom.dao.alias.impl;

import com.jzoom.zoom.dao.adapter.NameAdapter;

public class CamelNameAdapter implements NameAdapter {

	public static final NameAdapter ADAPTER = new CamelNameAdapter();
	private CamelAliasPolicy camelAliasPolicy = new CamelAliasPolicy();
	
	
	public CamelNameAdapter() {
	}

	@Override
	public String getFieldName(String column) {
		return camelAliasPolicy.getAlias(column);
	}

	@Override
	public String getColumnName(String field) {
		//反向命名
		char[] arr = field.toCharArray();
		StringBuilder sb = new StringBuilder();
		for(int i=0, c = arr.length ; i < c; ++i) {
			char ch = arr[i];
			if(Character.isUpperCase(ch)) {
				sb.append("_");
				sb.append(ch);
			}else {
				sb.append(Character.toUpperCase(ch));
			}
		}
		
		return sb.toString();
	}
	
	
	
}
