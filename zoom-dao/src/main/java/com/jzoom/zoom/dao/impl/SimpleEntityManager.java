package com.jzoom.zoom.dao.impl;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.jzoom.zoom.dao.Entity;
import com.jzoom.zoom.dao.EntityManager;
import com.jzoom.zoom.dao.annotation.Table;

/**
 * 简单点，如果找不到，那么就去绑定一下
 * @author jzoom
 *
 */
public class SimpleEntityManager implements EntityManager {
	
	private Map<Class<?>, Entity> map;
	
	public SimpleEntityManager(){
		map = new ConcurrentHashMap<Class<?>, Entity>();
	}

	@Override
	public Entity getEntity(Class<?> type, String table) {
		
		Entity entity = map.get( type );
		if(entity == null) {
			//这里绑定大概几毫秒，所以可以忍受同步
			synchronized ( this ) {
				entity = bind(type, table);
				map.put(type,entity);
				
			}
		}
		
		
		return entity;
	}

	@Override
	public Entity bind(Class<?> type, String table) {
		
		if(table == null) {
			Table annotation = type.getAnnotation(Table.class);
			if(annotation==null) {
				throw new RuntimeException(String.format("无法找到实体类%s的绑定信息，找不到对应的表,1、在查询的时候指定表 ar.table() 2、在实体类标注@Table", type));
			}
			table = annotation.name();
		}
		
		//找到对应的数据库结构
		
		
		
		
		return null;
	}

	
}
