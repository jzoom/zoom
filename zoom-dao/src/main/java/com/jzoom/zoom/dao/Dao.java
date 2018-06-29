package com.jzoom.zoom.dao;

import com.jzoom.zoom.dao.meta.TableMeta;

public interface Dao {
	
	/**
	 * 创建一个request范围的ActiveRecord
	 * @return
	 */
	Ar ar();
	
	Ar table(String table);
	
	/**
	 * 获取某一个类的实体对象
	 * @param entityClass
	 * @return
	 */
	Entity getEntity( Class<?> entityClass );
	
	/**
	 * 获取表元数据
	 * @return
	 */
	TableMeta getTableMeta( String table );
	
}
