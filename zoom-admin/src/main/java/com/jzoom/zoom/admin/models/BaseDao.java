package com.jzoom.zoom.admin.models;

import java.util.List;
import java.util.Map;

import com.jzoom.zoom.dao.Dao;
import com.jzoom.zoom.dao.Record;
import com.jzoom.zoom.dao.SqlBuilder.Sort;

public class BaseDao implements AdminModel<Record> {
	
	protected Dao dao;
	
	protected String table;
	
	protected String idName;
	
	public BaseDao( String table,String id) {
		this.table = table;
		this.idName = id;
	}
	
	public List<Record> getList(){
		return dao
				.table(table)
				.select("*")
				.orderBy(idName, Sort.DESC )
				.get();
	}

	public List<Record> getList(Map<String, Object> search) {
		Record record = new Record(search);
		String select  = record.getString("_select");
		return dao
				.table(table)
				.select(select == null ? "*" : select)
				.orderBy(idName, Sort.DESC )
				.get();
	}
	
	public int put(String id,Map<String, Object> data) {
		return dao.table(table).where(idName, id).setAll(data).update();
	}

	public int add(Map<String, Object> data) {
		return dao.table(table).insert(data);
	}

	public Record fetch(String id) {
		return dao.table(table).where(idName, id).fetch();
	}

	public int del(String id) {
		return dao.table(table).where(idName, id).delete();
	}

	public Dao getDao() {
		return dao;
	}

	public void setDao(Dao dao) {
		this.dao = dao;
	}

	
}
