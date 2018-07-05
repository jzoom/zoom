package com.jzoom.zoom.dao.driver;

import java.util.Collection;

import com.jzoom.zoom.dao.Ar;
import com.jzoom.zoom.dao.Record;
import com.jzoom.zoom.dao.meta.TableMeta;


public interface DbStructFactory {

	
	Collection<String> getTableNames( Ar ar );
	
	/**
	 * 获取tableMeta，最简单
	 * @param ar
	 * @param tableName
	 * @return
	 */
	TableMeta getTableMeta(Ar ar,String tableName);
	
	
	void fill(Ar ar,TableMeta meta);
	
	/**
	 * 获取表的名称和注释
	 * @param ar
	 * @return
	 */
	Collection<Record> getNameAndComments( Ar ar );
}
