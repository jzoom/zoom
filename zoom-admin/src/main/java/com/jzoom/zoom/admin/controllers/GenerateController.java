package com.jzoom.zoom.admin.controllers;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.beetl.core.Configuration;
import org.beetl.core.GroupTemplate;
import org.beetl.core.Template;
import org.beetl.core.resource.StringTemplateResourceLoader;

import com.jzoom.zoom.admin.entities.DecoTableVo;
import com.jzoom.zoom.admin.models.TableModel;
import com.jzoom.zoom.common.json.JSON;
import com.jzoom.zoom.dao.Record;
import com.jzoom.zoom.ioc.annonation.Inject;
import com.jzoom.zoom.web.annotation.Controller;
import com.jzoom.zoom.web.annotation.JsonResponse;
import com.jzoom.zoom.web.annotation.Mapping;

@Controller(key="gen")
public class GenerateController  implements AdminController{
	public GroupTemplate group;
	public GenerateController() {
		StringTemplateResourceLoader resourceLoader = new StringTemplateResourceLoader();
		try {
			group = new GroupTemplate(resourceLoader, Configuration.defaultConfiguration());
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	
	@Inject
	private TableModel tableModel;
	
	@SuppressWarnings("unchecked")
	@Mapping(value="template",method=Mapping.POST)
	@JsonResponse
	public int template(
			String table, 
			String type  ) {
		
		//查询一下template是否存在
		List<Record> list = tableModel.getTempltes(type);
		DecoTableVo data = tableModel.getTable(table,true);
		
		for (Record record : list) {
			Template t = group.getTemplate(record.getString("content"));
			Map<String, String> map = JSON.parse(JSON.stringify(data), Map.class);
			map.put("primaryKey", getPrimaryKey(data));
			t.binding(map);
			//插入记录
			String name = table + "/" + record.getString("name");
			tableModel.insertOrUpdate( name, t.render() );
		}
		
		
		return 0;
	}
	
	
	private String getPrimaryKey(DecoTableVo data) {
		String[] keys = data.getPrimaryKeys();
		if(keys.length == 0) {
			return "'id'";
		}
		if(keys.length == 1) {
			return String.format("'%s'", keys[0]);
		}
		
		StringBuilder sb = new StringBuilder();
		sb.append("[");
		boolean first = true;
		for (String key : keys) {
			if(first)
				first  =false;
			else {
				sb.append(",");
			}
			sb.append("'");
			sb.append(key);
			sb.append("'");
		}
		
		sb.append("]");
		return sb.toString();
	}
	
}
