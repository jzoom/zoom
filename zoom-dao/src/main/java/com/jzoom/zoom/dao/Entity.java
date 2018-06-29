package com.jzoom.zoom.dao;

import com.jzoom.zoom.dao.adapter.EntityAdapter;

/**
 * 绑定数据库字段和实体类字段
 * 
 * Column    ===========      Field
 * 解决：
 * 1、类型转化
 * 2、存储某一个类中的所有绑定关系
 * 
 * 
 * @author jzoom
 *
 */
public interface Entity {

	/**
	 * 表名声
	 * @return
	 */
	String getTableName();
	
	/**
	 * 实体类
	 * @return
	 */
	Class<?> getType();
	
	/**
	 * 所有绑定字段
	 * @return
	 */
	EntityAdapter[] getAdapters();
	
	
}
