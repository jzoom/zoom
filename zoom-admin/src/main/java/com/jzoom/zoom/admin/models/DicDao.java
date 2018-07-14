package com.jzoom.zoom.admin.models;

import java.util.List;
import java.util.Map;

import com.jzoom.zoom.dao.Record;

public class DicDao extends BaseDao {

	public DicDao() {
		super("sys_dic","type");
	}
	
	@Override
	public List<Record> getList() {
		List<Record> list = super.getList();
		for (Record r : list) {
			r.put("id", String.format("%s:%s", r.get("type"),r.get("item")));
		}
		return list;
	}
	
	@Override
	public Record fetch(String id) {
		String[] parts = id.split(":");
		return dao.table(table).where("type", parts[0]).where("item", parts[1]).fetch();
	}
	
	@Override
	public int put(String id, Map<String, Object> data) {
		String[] parts = id.split(":");
		return dao.table(table).where("type", parts[0]).where("item", parts[1]).setAll(data).update();
	}
	public int del(String id) {
		String[] parts = id.split(":");
		return dao.table(table).where("type", parts[0]).where("item", parts[1]).delete();
	}

}
