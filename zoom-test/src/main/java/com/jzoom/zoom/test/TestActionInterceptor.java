package com.jzoom.zoom.test;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.jzoom.zoom.common.json.JSON;
import com.jzoom.zoom.web.action.ActionContext;
import com.jzoom.zoom.web.action.ActionInterceptorAdapter;

/**
 * 针对json进行拦截，并记录在数据库里面
 * @author jzoom
 *
 */
public class TestActionInterceptor extends ActionInterceptorAdapter {
	
	@Override
	public void parse(ActionContext context) throws Exception {
		//只拦截json
		if(context.getPreParam() instanceof Map) {
			try {
				String json = JSON.stringify(context.getPreParam());
				HttpServletRequest request = context.getRequest();
				String path = request.getServletPath();
				context.put("path", path);
			} catch (Exception e) {
				//静悄悄
			}
		}
	}
	
}
