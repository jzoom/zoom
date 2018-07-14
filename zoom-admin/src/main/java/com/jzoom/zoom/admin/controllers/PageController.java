package com.jzoom.zoom.admin.controllers;

import com.jzoom.zoom.admin.models.PageDao;
import com.jzoom.zoom.web.annotation.Controller;
import com.jzoom.zoom.web.annotation.Mapping;

@Controller(key="page")
public class PageController extends BaseDaoController<PageDao>{
	
	
	@Mapping(value="template",method=Mapping.GET)
	public void template() {
		
	}
	
}
