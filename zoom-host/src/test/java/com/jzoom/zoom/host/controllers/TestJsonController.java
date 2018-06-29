package com.jzoom.zoom.host.controllers;

import com.jzoom.zoom.web.annotation.ActionFactory;
import com.jzoom.zoom.web.annotation.Controller;
import com.jzoom.zoom.web.annotation.Doc;
import com.jzoom.zoom.web.annotation.JsonResponse;
import com.jzoom.zoom.web.annotation.Mapping;
import com.jzoom.zoom.web.annotation.Param;
import com.jzoom.zoom.web.annotation.Template;

@Controller()
public class TestJsonController {

	@Mapping("/")
	@Template("index.html")
	public void index() {
		
	}


	@JsonResponse()
	public void test5(String id) {
		
		System.out.print(id);
		
	}
	

}
