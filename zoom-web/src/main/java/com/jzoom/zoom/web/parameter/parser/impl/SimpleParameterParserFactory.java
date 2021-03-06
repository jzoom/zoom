package com.jzoom.zoom.web.parameter.parser.impl;

import java.lang.reflect.Method;

import org.jzoom.zoom.common.Destroyable;

import com.jzoom.zoom.web.parameter.ParameterParser;
import com.jzoom.zoom.web.parameter.ParameterParserFactory;

public class SimpleParameterParserFactory implements ParameterParserFactory,Destroyable {

	@Override
	public ParameterParser createParamParser(Class<?> controllerClass, Method method, String[] names) {
		
		return new AutoParameterParser(controllerClass, method, names);
	}

	@Override
	public void destroy() {
		// TODO Auto-generated method stub
		
	}

}
