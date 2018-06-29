package com.jzoom.zoom.web.parameter.adapter.impl;

import com.jzoom.zoom.web.action.ActionContext;
import com.jzoom.zoom.web.parameter.adapter.ParameterAdapter;

public class BasicParameterAdapter {
	public static final ParameterAdapter<Object> EQ = new EqualAdapter();
	public static final ParameterAdapter<Object> REQUEST = new RequestAdapter();
	public static final ParameterAdapter<Object> RESPONSE = new ResponseAdapter();
	public static final ParameterAdapter<Object> ACTION_CONTEXT = new ActionContextAdapter();
	
	
	static class EqualAdapter implements ParameterAdapter<Object>{

		@Override
		public Object get(ActionContext context, Object data, String name, Class<?> type) {
		
			return data;
		}
		
	}
	
	static class RequestAdapter implements ParameterAdapter<Object>{

		@Override
		public Object get(ActionContext context, Object data, String name, Class<?> type) {
		
			return context.getRequest();
		}
		
	}

	
	static class ResponseAdapter implements ParameterAdapter<Object>{

		@Override
		public Object get(ActionContext context, Object data, String name, Class<?> type) {
		
			return context.getResponse();
		}
		
	}

	
	static class ActionContextAdapter implements ParameterAdapter<Object>{

		@Override
		public Object get(ActionContext context, Object data, String name, Class<?> type) {
		
			return context;
		}
		
	}

}
