package com.jzoom.zoom.admin.controllers;

import java.util.List;
import java.util.Map;

import com.jzoom.zoom.admin.models.ModDao;
import com.jzoom.zoom.common.json.JSON;
import com.jzoom.zoom.common.utils.MapUtils;
import com.jzoom.zoom.dao.Record;
import com.jzoom.zoom.ioc.annonation.Inject;
import com.jzoom.zoom.web.annotation.Controller;
import com.jzoom.zoom.web.annotation.JsonResponse;
import com.jzoom.zoom.web.annotation.Mapping;
import com.jzoom.zoom.web.annotation.Param;

@Controller(key="menu")
public class MenuController {

	@Inject
	ModDao moduleDao;
	
	@Mapping(value="index",method= {Mapping.GET})
	public Map<String, Object> index(){
		List<Record> list =  moduleDao.getList();
		return MapUtils.asMap("tree",JSON.stringify(list));
	}
	
	@JsonResponse
	public List<Record> list(){
		return moduleDao.getList();
	}
	
	@JsonResponse
	@Mapping(value="add",method= {Mapping.POST})
	public void add( 
			@Param( comment="提交的表单信息",name=Param.BODY)  Map<String, Object> data
		) {
	
		moduleDao.add( data );
		
		
	}
	
}
