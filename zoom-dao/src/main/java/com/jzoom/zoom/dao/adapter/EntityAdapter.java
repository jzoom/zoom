package com.jzoom.zoom.dao.adapter;

import com.jzoom.zoom.caster.ValueCaster;

public interface EntityAdapter extends StatementAdapter,ValueCaster {

	/**
	 * 从对应的实例中取出字段对应的值
	 * @param target
	 * @return
	 */
	Object get(Object target);
	
	String getColumnName();
	
	
}
