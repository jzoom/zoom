package com.jzoom.zoom.admin.models;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.jzoom.zoom.common.filter.ArrayFilter;
import com.jzoom.zoom.common.filter.Filter;
import com.jzoom.zoom.common.filter.pattern.PatternFilterFactory;
import com.jzoom.zoom.dao.Ar;
import com.jzoom.zoom.dao.Dao;
import com.jzoom.zoom.dao.Page;
import com.jzoom.zoom.dao.Record;
import com.jzoom.zoom.dao.SqlBuilder.Sort;
import com.jzoom.zoom.dao.driver.DbStructFactory;
import com.jzoom.zoom.dao.utils.DaoUtils;
import com.jzoom.zoom.ioc.annonation.Inject;

public class DecoTableDao {
	@Inject
	private Dao dao;
	
	@Inject("admin")
	private Dao admin;
	
	
	public List<Record> getDecorated(Map<String, Object> params){
		Ar ar = admin.table("sys_deco_table")
				.select("target_table as table,comment")
				.orderBy("target_table", Sort.ASC );
		BaseDao.parseWhere(ar, params); 
		//查询表
		List<Record> decos = ar.get();
		return decos;
	}
	
	
	public Page<Record> getList(Map<String, Object> params ) {
		DbStructFactory factory = dao.getDbStructFactory();
		Collection<Record> records= factory.getNameAndComments(dao.ar());
		final String name = (String) params.get("target_table");
		
		if(name != null) {
			final Filter<String> filter = PatternFilterFactory.createFilter("*"+name+"*");
			records = ArrayFilter.filter(records, new Filter<Record>() {
				@Override
				public boolean accept(Record value) {
					return filter.accept((String)value.get("name"));
				}
				
			});
		}
		List<Record> decos = getDecorated(params);
		Map<Object, Record> map = DaoUtils.list2map(decos, "table");
		
		for (Record record : decos) {
			String table = record.getString("table");
			Record src = map.get(table);
			if(src!=null) {
				src.put("comment", record.get("comment"));
				src.put("decoration", true);
			}
		}
		List<Record> list = new ArrayList<Record>();
		list.addAll(records);
		return new Page<Record>(list, 1, 1000, list.size());
	}
	
}
