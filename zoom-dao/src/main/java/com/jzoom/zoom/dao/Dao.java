package com.jzoom.zoom.dao;

import com.jzoom.zoom.dao.alias.AliasPolicyManager;
import com.jzoom.zoom.dao.driver.DbStructFactory;

public interface Dao extends AliasPolicyManager {
	
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
	 * 获取数据库结构
	 * @return
	 */
	DbStructFactory getDbStructFactory() ;
	
}
