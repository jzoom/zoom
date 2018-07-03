package com.jzoom.zoom.admin.controllers;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.jzoom.zoom.dao.Dao;
import com.jzoom.zoom.dao.Record;
import com.jzoom.zoom.dao.driver.DbStructFactory;
import com.jzoom.zoom.dao.meta.TableMeta;
import com.jzoom.zoom.dao.utils.DaoUtils;
import com.jzoom.zoom.ioc.annonation.Inject;
import com.jzoom.zoom.web.annotation.Controller;
import com.jzoom.zoom.web.annotation.JsonResponse;
import com.jzoom.zoom.web.annotation.Mapping;
import com.jzoom.zoom.web.annotation.Param;

@Controller(key="deco_table")
public class DecoTableController {

	@Inject
	private Dao dao;
	

	@Mapping(value="index",method= {Mapping.GET})
	public Object index(){
		return null;
	}
	
	@Mapping(value="add",method= {Mapping.GET})
	public Object add() {
		return null;
	}
	
	@Mapping(value="edit",method= {Mapping.GET})
	public Object edit() {
		return null;
	}
	
	@JsonResponse
	@Mapping(value="index",method= {Mapping.POST})
	public Collection<Record> list(){
		DbStructFactory factory = dao.getDbStructFactory();
		Collection<Record> records= factory.getNameAndComments(dao.ar());
		//查询表
		List<Record> decos = dao.table("sys_deco_table").select("target_table as table,comment").get();
		
		Map<Object, Record> map = DaoUtils.list2map(decos, "table");
		
		for (Record record : decos) {
			String table = record.getString("table");
			Record src = map.get(table);
			if(src!=null) {
				src.put("comment", record.get("comment"));
				src.put("decoration", true);
			}
		}
		return records;
	}
	
	//查询所有的表和所有的deco整合起来

	@JsonResponse
	@Mapping(value="get/{table}",method=Mapping.POST)
	public TableMeta get( @Param(name="{table}") String table  ) {
		
		return dao.getDbStructFactory().getTableMeta(dao.ar(), table);
	}
	
	
	
}
