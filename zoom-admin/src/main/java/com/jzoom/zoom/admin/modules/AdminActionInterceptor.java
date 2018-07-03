package com.jzoom.zoom.admin.modules;

import javax.servlet.http.HttpServletRequest;

import com.jzoom.zoom.admin.models.TokenService;
import com.jzoom.zoom.ioc.annonation.Inject;
import com.jzoom.zoom.token.hex.ClientToken;
import com.jzoom.zoom.web.action.ActionContext;
import com.jzoom.zoom.web.action.ActionInterceptorAdapter;
import com.jzoom.zoom.web.exception.StatusException;

public class AdminActionInterceptor extends ActionInterceptorAdapter {
	
	@Inject
	private TokenService tokenService;

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
		
		
		context.put("token", clientToken);
		context.put("userId", clientToken.getId());
		
		return true;
	}
}
