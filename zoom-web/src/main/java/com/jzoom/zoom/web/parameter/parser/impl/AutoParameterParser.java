package com.jzoom.zoom.web.parameter.parser.impl;

import java.lang.reflect.Method;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.jzoom.zoom.common.Destroyable;

import com.jzoom.zoom.web.action.ActionContext;
import com.jzoom.zoom.web.parameter.ParameterParser;

public class AutoParameterParser implements ParameterParser ,Destroyable {
	
	private static final MapParameterParserFactory mapFactory = new MapParameterParserFactory();
	private static final FormParameterParserFactory formFactory = new FormParameterParserFactory();
	
	private ParameterParser form;
	private ParameterParser map;
	
	private Class<?> controllerClass;
	private Method method;
	private String[] names;
	
	public AutoParameterParser( Class<?> controllerClass,Method method,String[] names) {
		this.controllerClass = controllerClass;
		this.method = method;
		this.names = names;
		for (String name : names) {
			if(name==null) {
				throw new RuntimeException("name 不能为null " + controllerClass + ":" + method);
			}
		}
	}
	@Override
	public void destroy() {
		this.method = null;
	}


	@Override
	public Object[] parse(ActionContext context) throws Exception {
		final Object param = context.getPreParam();
		if(param instanceof HttpServletRequest) {
			if(form == null) {
				synchronized (this) {
					form = formFactory.createParamParser(controllerClass, method, names);
				}
			}
			
			return form.parse(context);
		}else if(param instanceof Map) {
			
			if(map == null) {
				synchronized (this) {
					map = mapFactory.createParamParser(controllerClass, method, names);
				}
			}
			return map.parse(context);
		}else {
			throw new RuntimeException("不支持的解析参数类型");
		}
	}



}
