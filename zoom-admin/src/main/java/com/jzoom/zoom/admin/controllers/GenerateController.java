package com.jzoom.zoom.admin.controllers;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.BeanUtils;
import org.beetl.core.Configuration;
import org.beetl.core.GroupTemplate;
import org.beetl.core.Template;
import org.beetl.core.resource.StringTemplateResourceLoader;

import com.jzoom.zoom.admin.entities.DecoTableVo;
import com.jzoom.zoom.admin.models.TableModel;
import com.jzoom.zoom.common.json.JSON;
import com.jzoom.zoom.common.utils.CachedClasses;
import com.jzoom.zoom.dao.Record;
import com.jzoom.zoom.ioc.annonation.Inject;
import com.jzoom.zoom.web.annotation.Controller;
import com.jzoom.zoom.web.annotation.JsonResponse;
import com.jzoom.zoom.web.annotation.Mapping;
import com.jzoom.zoom.web.annotation.Param;
import com.jzoom.zoom.web.rendering.impl.BeetlRendering;
import com.jzoom.zoom.web.view.impl.StringView;
import com.mchange.v2.beans.BeansUtils;

@Controller(key="gen")
public class GenerateController {
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
			t.binding(map);
			//插入记录
			String name = table + "/" + record.getString("name");
			tableModel.insertOrUpdate( name, t.render() );
			
			
		//	System.out.print(t.render());
			//
		}
		
		
		return 0;
	}
	
}
