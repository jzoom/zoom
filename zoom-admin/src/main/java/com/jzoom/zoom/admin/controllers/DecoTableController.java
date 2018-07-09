package com.jzoom.zoom.admin.controllers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.jzoom.zoom.admin.entities.DecoTableVo;
import com.jzoom.zoom.admin.entities.DecoTableVo.DecoColumn;
import com.jzoom.zoom.dao.Dao;
import com.jzoom.zoom.dao.Record;
import com.jzoom.zoom.dao.adapter.NameAdapter;
import com.jzoom.zoom.dao.driver.DbStructFactory;
import com.jzoom.zoom.dao.meta.ColumnMeta;
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
	
	@Inject("admin")
	private Dao admin;
	

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
		List<Record> decos = admin.table("sys_deco_table").select("target_table as table,comment").get();
		
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
	public DecoTableVo get( @Param(name="{table}") String table  ) {
		
		TableMeta data =  dao.getDbStructFactory().getTableMeta(dao.ar(), table);
		//NameAdapter adapter = dao.getPolicy(table);
		
		//下发数据
		Record record = admin.table("sys_deco_table").where("target_table", table).fetch();
		List<Record> columns = admin.table("sys_decoration").where("target_table",table).get();
		
		DecoTableVo vo = new DecoTableVo();
		vo.setComment(data.getComment());
		vo.setName(data.getName());
		List<DecoColumn> list = new ArrayList<DecoTableVo.DecoColumn>();
		Map<String, DecoColumn> map = new HashMap<String, DecoColumn>();
		for (ColumnMeta columnMeta : data.getColumns()) {
			DecoColumn decoColumn = new DecoColumn();
			decoColumn.setName( columnMeta.getName() );
			decoColumn.setComment(columnMeta.getComment());
			map.put(columnMeta.getName(), decoColumn);
			list.add(decoColumn);
		}
		vo.setColumns(list);
		if(record!=null) {
			vo.setComment(record.getString("comment"));
			if(columns.size() > 0 ) {
				for (Record record2 : columns) {
					String name = record2.getString("target_column");
					DecoColumn decoColumn = map.get(name);
					decoColumn.setComment(record2.getString("comment"));
					decoColumn.setType(record2.getString("type"));
				}
			}
		}
		
		return vo;
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
			String targetColumn = (String) map.remove("name");
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
		
		//保存在表中
//		Record record = admin.table("sys_deco_table").where("target_table", name).fetch();
	//	List<Record> columns = admin.table("sys_decoration").where("target_table",name).get();
		
		
		
		return 0;
	}
	
}
