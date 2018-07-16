package com.jzoom.zoom.admin.controllers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.jzoom.zoom.admin.entities.DecoTableVo;
import com.jzoom.zoom.admin.models.TableModel;
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
import com.jzoom.zoom.web.annotation.Controller;
import com.jzoom.zoom.web.annotation.JsonResponse;
import com.jzoom.zoom.web.annotation.Mapping;
import com.jzoom.zoom.web.annotation.Param;

@Controller(key="deco_table")
public class DecoTableController  implements AdminController{

	@Inject
	private Dao dao;
	
	@Inject("admin")
	private Dao admin;
	
	@Inject
	private TableModel tableModel;
	

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
	public Page<Record> list( @Param(name="@") Map<String, Object> params ){
		DbStructFactory factory = dao.getDbStructFactory();
		Collection<Record> records= factory.getNameAndComments(dao.ar());
		final String name = (String) params.get("name");
		
		if(name != null) {
			final Filter<String> filter = PatternFilterFactory.createFilter("*"+name+"*");
			records = ArrayFilter.filter(records, new Filter<Record>() {
				@Override
				public boolean accept(Record value) {
					return filter.accept((String)value.get("name"));
				}
				
			});
		}
		Ar ar = admin.table("sys_deco_table")
				.select("target_table as table,comment")
				.orderBy("target_table", Sort.ASC );
		//查询表
		List<Record> decos = ar.get();
		
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
		return new Page<Record>(list, 0, 0, 0);
	}
	
	//查询所有的表和所有的deco整合起来
	@JsonResponse
	@Mapping(value="get/{table}",method=Mapping.POST)
	public DecoTableVo get( @Param(name="{table}") String table  ) {
		
		return tableModel.getTable(table,false);
	}
	
	@JsonResponse
	@Mapping(value="put/{name}",method=Mapping.POST)
	public int put(@Param(name="{name}") String name,  String comment,  List<Map<String, Object>> columns ) {
		
		int count = admin.table("sys_deco_table").set("comment", comment)
			.where("target_table", name).update();
		if(count <= 0) {
			admin.table("sys_deco_table").set("comment",comment).set("target_table", name).insert();
		}
		
		for (Map<String, Object> map : columns) {
			String targetColumn = (String) map.remove("column");
			map.remove("name");
			count = admin.table("sys_decoration")
					.setAll(map)
					.where("target_table", name)
					.where("target_column", targetColumn)
					.update();
			if(count <= 0) {
				admin.table("sys_decoration")
					.set("target_table", name)
					.set("target_column", targetColumn)
					.setAll(map)
					.insert();
			}
		}
		
		
		return 1;
	}
	
}
