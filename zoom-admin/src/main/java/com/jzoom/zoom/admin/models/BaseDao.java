package com.jzoom.zoom.admin.models;

import java.util.List;
import java.util.Map;

import com.jzoom.zoom.dao.Dao;
import com.jzoom.zoom.dao.Record;
import com.jzoom.zoom.dao.SqlBuilder.Sort;

public class BaseDao implements AdminModel<Record> {
	
	private Dao dao;
	
	private String table;
	
	public BaseDao( String table) {
		this.table = table;
	}
	
	public List<Record> getList(){
		return dao
				.table(table)
				.select("*")
				.orderBy("id", Sort.DESC )
				.get();
	}

	public List<Record> getList(Map<String, Object> search) {
		Record record = new Record(search);
		String select  = record.getString("_select");
		return dao
				.table(table)
				.select(select == null ? "*" : select)
				.orderBy("id", Sort.DESC )
				.get();
	}
	
	public int put(String id,Map<String, Object> data) {
		return dao.table(table).where("id", id).setAll(data).update();
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
