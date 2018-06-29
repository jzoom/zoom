package com.jzoom.zoom.dao.driver.mysql;

import com.jzoom.zoom.dao.adapter.StatementAdapter;
import com.jzoom.zoom.dao.driver.AbsDriver;

public class MysqlDriver  extends AbsDriver{

	@Override
	public StringBuilder protectColumn(StringBuilder sb, String name) {
		return sb.append('`').append(name).append('`');
	}

	@Override
	public StatementAdapter get(Class<?> dataClass, Class<?> columnClass) {
		return super.get(dataClass, columnClass);
	}

}
