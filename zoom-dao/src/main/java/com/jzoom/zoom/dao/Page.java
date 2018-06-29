package com.jzoom.zoom.dao;

import java.util.List;

public interface Page<T> {

	/**
	 * 列表
	 * @return
	 */
	List<Record> getList();
	
	int getPage();
	
	int getPageSize();
	
	int getTotalRows();
	
	
	
	
	
}
