package com.jzoom.zoom.web.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 一个路由单元
 * url+METHOD对应到一个ActionHandler
 * 
 * @author jzoom
 *
 */
public interface ActionHandler {

	boolean handle(HttpServletRequest request, HttpServletResponse response);
	

}
