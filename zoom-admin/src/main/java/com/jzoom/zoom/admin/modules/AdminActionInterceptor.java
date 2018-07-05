package com.jzoom.zoom.admin.modules;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.ArrayUtils;

import com.jzoom.zoom.admin.models.TokenService;
import com.jzoom.zoom.dao.Dao;
import com.jzoom.zoom.dao.Record;
import com.jzoom.zoom.ioc.annonation.Inject;
import com.jzoom.zoom.token.hex.ClientToken;
import com.jzoom.zoom.web.action.ActionContext;
import com.jzoom.zoom.web.action.ActionInterceptorAdapter;
import com.jzoom.zoom.web.exception.StatusException;

public class AdminActionInterceptor extends ActionInterceptorAdapter {
	
	@Inject
	private TokenService tokenService;
	
	@Inject
	private Dao dao;

	@Override
	public boolean preParse(ActionContext context) throws Exception {
		
		HttpServletRequest request = context.getRequest();
		String token = request.getHeader("token");
		
		ClientToken clientToken = tokenService.verifyToken(token);
		
		if(clientToken == null) {
			throw new StatusException.UnAuthException();
		}
		
		if(clientToken.isTimeout()) {
			throw new StatusException.AuthException();
		}
		//检查权限是否满足条件
		String mods = dao.table("sys_user").where("sys_user.id", clientToken.getId())
			.join( "sys_role","sys_role.id=sys_user.rl_id" )
			.getValue("mods", String.class);
		if(mods==null) {
			throw new StatusException.AuthException();
		}
		String[] parts = mods.split(",");
		String path = context.getRequest().getServletPath();
		if(path.startsWith("/")) {
			path = path.substring(1);
		}
		int n;
		if( ((n = path.indexOf('/')) > 0) ) {
			path = path.substring(0,n);
		}
		
		String id = dao.table("sys_mod").where("url", path)
				.orWhere("url",path+"/index")
				.getValue("id", String.class);
		if(ArrayUtils.indexOf(parts, id) < 0) {
			throw new StatusException.AuthException();
		}
		//dao.table("sys_mod").where("", context.getAction().getUrl());
		
		
		context.put("token", clientToken);
		context.put("userId", clientToken.getId());
		context.put("mods", parts);
		
		return true;
	}
}
