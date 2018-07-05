package com.jzoom.zoom.dao.driver;

import java.util.List;

import com.jzoom.zoom.dao.adapter.StatementAdapter;
import com.jzoom.zoom.dao.meta.TableMeta;

public interface SqlDriver {

	/**
	 * 保护字段，如mysql加上 `` oracle有可能需要加上""
	 * @param name
	 * @return
	 */
	StringBuilder protectColumn( StringBuilder sb, String name);
	
	
	StringBuilder protectTable( StringBuilder sb, String name);
	/**
	 * 获取数据适配器
	 * @param dataClass
	 * @param columnClass
	 * @return
	 */
	StatementAdapter get( Class<?> dataClass, Class<?> columnClass );

	
}
