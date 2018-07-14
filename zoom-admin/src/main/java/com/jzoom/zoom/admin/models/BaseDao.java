package com.jzoom.zoom.admin.models;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.jzoom.zoom.caster.Caster;
import com.jzoom.zoom.dao.Ar;
import com.jzoom.zoom.dao.Dao;
import com.jzoom.zoom.dao.Page;
import com.jzoom.zoom.dao.Record;
import com.jzoom.zoom.dao.SqlBuilder.Sort;

public class BaseDao implements AdminModel<Record> {

	protected Dao dao;

	protected String table;

	protected String idName;

	public BaseDao(String table, String id) {
		this.table = table;
		this.idName = id;
	}

	public List<Record> getList() {
		return dao.table(table).select("*").orderBy(idName, Sort.DESC).get();
	}
	public List<Record> getList(Map<String, Object> search) {
		Ar ar = parseSearch(search);
		return ar.get();
	}
	
	protected Ar parseSearch(Map<String, Object> search) {
		Record record = new Record(search);
		String select = record.getString("_select");
		
		String order = Caster.to(search.get("_order"), String.class);
		String sort = Caster.to(search.get("_sort"), String.class);

		Ar ar = dao.table(table).select(select == null ? "*" : select).orderBy(idName, Sort.DESC);
		if(order!=null && sort!=null) {
			
		}
		for (Entry<String, Object> entry : search.entrySet()) {

		}
		ar.orderBy(idName, Sort.DESC);
		return ar;
		
	}
	
	public Page<Record> getPage(Map<String, Object> search){
		Ar ar = parseSearch(search);
		int pageSize = Caster.to(search.get("_pageSize"), int.class);
		if(pageSize <=0 ) {
			pageSize = 30;
		}
		int page = Caster.to(search.get("_page"), int.class);
		return ar.page(page, pageSize);
	}

//	public List<Record> getList(Map<String, Object> search) {
//		Record record = new Record(search);
//		String select = record.getString("_select");
//		int pageSize = Caster.to(search.get("_pageSize"), int.class);
//		int page = Caster.to(search.get("_page"), int.class);
//		String order = Caster.to(search.get("_order"), String.class);
//		String sort = Caster.to(search.get("_sort"), String.class);
//
//		Ar ar = dao.table(table).select(select == null ? "*" : select).orderBy(idName, Sort.DESC);
//		if(order!=null && sort!=null) {
//			
//		}
//		for (Entry<String, Object> entry : search.entrySet()) {
//
//		}
//		
//		return ar.get();
//	}

	public int put(String id, Map<String, Object> data) {
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
