package com.jzoom.zoom.dao;

import java.util.List;

import com.jzoom.zoom.dao.driver.DbStructFactory;
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
	 * 获取数据库结构
	 * @return
	 */
	DbStructFactory getDbStructFactory() ;
	
}
