package com.jzoom.zoom.dao;

public interface Trans {

	void beginTransaction(int level);
	
	void commit();
	
	void rollback();
	
}
