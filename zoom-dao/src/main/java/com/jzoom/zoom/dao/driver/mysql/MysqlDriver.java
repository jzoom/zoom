package com.jzoom.zoom.dao.driver.mysql;

import java.util.Collection;
import java.util.List;

import com.jzoom.zoom.dao.Ar;
import com.jzoom.zoom.dao.adapter.StatementAdapter;
import com.jzoom.zoom.dao.driver.AbsDriver;
import com.jzoom.zoom.dao.meta.TableMeta;

public class MysqlDriver  extends AbsDriver{

	@Override
	public StringBuilder protectName(StringBuilder sb, String name) {
		return sb.append('`').append(name).append('`');
	}

	@Override
	public StatementAdapter get(Class<?> dataClass, Class<?> columnClass) {
		return super.get(dataClass, columnClass);
	}

	@Override
	public void fill(TableMeta meta) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public List<TableMeta> getTableMetas() {
		
		return null;
	}

	

	

}
