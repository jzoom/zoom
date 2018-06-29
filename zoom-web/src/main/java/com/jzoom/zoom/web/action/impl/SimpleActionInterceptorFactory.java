package com.jzoom.zoom.web.action.impl;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import com.jzoom.zoom.common.filter.impl.ClassAndMethodFilter;
import com.jzoom.zoom.common.utils.OrderedList;
import com.jzoom.zoom.web.action.ActionInterceptor;
import com.jzoom.zoom.web.action.ActionInterceptorFactory;

class SimpleActionInterceptorFactory implements ActionInterceptorFactory {
	
	private OrderedList<InterceptorInfo> list;
	private InterceptorInfo[] interceptors;
	private List<ActionInterceptor> tmp;
	
	private static class InterceptorInfo{
		InterceptorInfo(ActionInterceptor interceptor,String pattern) {
			this.interceptor = interceptor;
			this.filter = new ClassAndMethodFilter(pattern);
		}
		
		ActionInterceptor interceptor;
		ClassAndMethodFilter filter;
	}
	
	public SimpleActionInterceptorFactory() {
		list = new OrderedList<InterceptorInfo>();
		tmp = new ArrayList<ActionInterceptor>();
	}
	
	
	@Override
	public void add(ActionInterceptor interceptor, String pattern, int order) {
		list.add(new InterceptorInfo(interceptor, pattern), order);
	}

	@Override
	public ActionInterceptor[] create(Class<?> controllerClass, Method method) {
		final List<ActionInterceptor> tmp  = this.tmp;
		
		InterceptorInfo[] interceptors = this.interceptors;
		tmp.clear();
		
		if(interceptors==null) {
			interceptors = list.toArray( new InterceptorInfo[ list.size() ] );
		}
		for (InterceptorInfo interceptorInfo : interceptors) {
			if(interceptorInfo.filter.accept(controllerClass) && interceptorInfo.filter.accept(method)) {
				tmp.add(interceptorInfo.interceptor);
			}
		}
		
		if(tmp.size() == 0) {
			return null;
		}
		
		
		return tmp.toArray( new ActionInterceptor[ tmp.size() ] );
	}

	

}
