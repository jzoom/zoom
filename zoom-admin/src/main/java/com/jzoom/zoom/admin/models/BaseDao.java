package com.jzoom.zoom.admin.models;

import java.util.List;
import java.util.Map;

import com.jzoom.zoom.dao.Dao;
import com.jzoom.zoom.dao.Record;
import com.jzoom.zoom.dao.SqlBuilder.Sort;
import com.jzoom.zoom.ioc.annonation.Inject;

public class BaseDao {
	@Inject
	private Dao dao;
	
	private String table;
	
	public BaseDao(String table) {
		this.table = table;
	}
	
	public List<Record> getList(){
		return dao
				.table(table)
				.select("*")
				.orderBy("id", Sort.DESC )
				.get();
	}
	
	public void put(String id,Map<String, Object> data) {
		dao.table(table).where("id", id).setAll(data).update();
	}

	public int add(Map<String, Object> data) {
		return dao.table(table).insert(data);
	}

	public Record fetch(String id) {
		return dao.table(table).where("id", id).fetch();
	}

	public int del(String id) {
		return dao.table(table).where("id", id).delete();
	}

	public Dao getDao() {
		return dao;
	}

	public void setDao(Dao dao) {
		this.dao = dao;
	}
	
	
}
