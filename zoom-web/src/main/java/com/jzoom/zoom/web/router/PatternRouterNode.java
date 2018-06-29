package com.jzoom.zoom.web.router;

import javax.servlet.http.HttpServletRequest;

import com.jzoom.zoom.web.action.ActionHandler;

public class PatternRouterNode extends RouterNode {

	private String paramName;
	
	public PatternRouterNode(int level,String paramName) {
		super(level);
		this.paramName = paramName;
	}
	
	@Override
	public ActionHandler match(String[] parts, HttpServletRequest request) {
		request.setAttribute(paramName, parts[level-1]);
		
		return super.match(parts, request);
	}

}
