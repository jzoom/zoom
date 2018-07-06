package com.jzoom.zoom.admin.controllers;

import com.jzoom.zoom.admin.models.AdminException;
import com.jzoom.zoom.admin.models.TokenService;
import com.jzoom.zoom.dao.Dao;
import com.jzoom.zoom.dao.Record;
import com.jzoom.zoom.ioc.annonation.Inject;
import com.jzoom.zoom.web.annotation.Controller;
import com.jzoom.zoom.web.annotation.JsonResponse;
import com.jzoom.zoom.web.annotation.Mapping;

@Controller(key="login")
public class LoginController {

	@Inject("admin")
	private Dao dao;
	
	@Inject
	private TokenService tokernService;
	
	@JsonResponse
	@Mapping(value="",method=Mapping.POST)
	public String doLogin(String account,String pwd) throws AdminException {
		
		Record record = dao.table("sys_user").where("account", account).where("pwd", pwd).fetch();
		if(record==null) {
			throw new AdminException("登录失败");
		}
		//生成token
		return tokernService.generateToken(record.getString("id"));
		
	}
	
}
