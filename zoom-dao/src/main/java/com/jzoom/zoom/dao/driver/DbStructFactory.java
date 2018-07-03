package com.jzoom.zoom.dao.driver;

import java.util.Collection;

import com.jzoom.zoom.dao.Ar;
import com.jzoom.zoom.dao.Record;
import com.jzoom.zoom.dao.meta.TableMeta;


public interface DbStructFactory {

	String getComment(String tableName);
	
	Collection<String> getTableNames();
	
	TableMeta getTableMeta(Ar ar,String tableName);
	
	/**
	 * 获取表的名称和注释
	 * @param ar
	 * @return
	 */
	Collection<Record> getNameAndComments( Ar ar );
}
