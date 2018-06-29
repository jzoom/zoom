package com.jzoom.zoom.dao;

/**
 * 管理Entity ，table和实体类的绑定关系，注意：
 * 1、一个实体类支持绑定多张表
 * 2、默认情况下是根据  {@link com.jzoom.zoom.dao.annotation.Table} 来绑定
 * @author jzoom
 *
 */
public interface EntityManager {

	/**
	 * 根据实体类和表，获取到一个Entity绑定关系,
	 * @param type
	 * @param table
	 * @return
	 */
	Entity getEntity(Class<?> type,String table);
	
	/**
	 * 将一个实体Class和一张表进行绑定
	 * @param type
	 * @param table
	 */
	Entity bind(Class<?> type,String table);
	
}
