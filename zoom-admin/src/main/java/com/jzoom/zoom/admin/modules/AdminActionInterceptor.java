package com.jzoom.zoom.admin.modules;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.ArrayUtils;

import com.jzoom.zoom.admin.models.TokenService;
import com.jzoom.zoom.dao.Ar;
import com.jzoom.zoom.dao.Dao;
import com.jzoom.zoom.ioc.annonation.Inject;
import com.jzoom.zoom.token.hex.ClientToken;
import com.jzoom.zoom.web.action.ActionContext;
import com.jzoom.zoom.web.action.ActionInterceptorAdapter;
import com.jzoom.zoom.web.exception.StatusException;

public class AdminActionInterceptor extends ActionInterceptorAdapter {
	
	@Inject
	private TokenService tokenService;
	
	@Inject("admin")
	private Dao dao;
	
	
	
	private String getUrl(String path) {
		//如果是一般的增删查改，则使用模糊匹配就行了
		String[] parts = path.split("\\/");
		if(parts.length >= 2 && parts.length <= 3) {
			String method = parts[1];
			if("add".equals(method) || "index".equals(method) || "edit".equals(method)
					|| "put".equals(method) || "del".equals(method)
					|| "get".equals(method) || "list".equals(method)) {
				
				int n;
				if( ((n = path.indexOf('/')) > 0) ) {
					path = path.substring(0,n);
				}

				return path + "/index";
			}
				
		}
		return path;
	}

	@Override
	public boolean preParse(ActionContext context) throws Exception {
		
		HttpServletRequest request = context.getRequest();
		String token = request.getHeader("token");
		
		ClientToken clientToken = tokenService.verifyToken(token);
		
		if(clientToken == null) {
			throw new StatusException.UnAuthException();
		}
		
		if(clientToken.isTimeout()) {
			throw new StatusException.UnAuthException();
		}
		//检查权限是否满足条件
		String mods = dao.table("sys_user").where("sys_user.id", clientToken.getId())
			.join( "sys_role","sys_role.id=sys_user.rl_id" )
			.getValue("mods", String.class);
		if(mods==null) {
			throw new StatusException.AuthException(  );
		}
		String[] parts = mods.split(",");
		String path = context.getRequest().getServletPath();
		if(path.startsWith("/")) {
			path = path.substring(1);
		}
		String url = getUrl(path);
		Ar ar = dao.table("sys_mod").where("url", url);
		if(!url.equals(path)) {
			ar.orWhere("url", path);
		}
		ar.orWhere("url",context.getAction().getPath());
		String id = ar.getValue("id", String.class);
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
