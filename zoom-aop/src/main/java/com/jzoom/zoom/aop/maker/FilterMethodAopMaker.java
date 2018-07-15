package com.jzoom.zoom.aop.maker;

import java.lang.reflect.Method;
import java.util.List;

import com.jzoom.zoom.aop.AopMaker;
import com.jzoom.zoom.aop.MethodInterceptor;
import com.jzoom.zoom.common.filter.impl.PatternClassAndMethodFilter;

public class FilterMethodAopMaker implements AopMaker {
	

	private PatternClassAndMethodFilter filter;
	private MethodInterceptor interceptor;
	
	
	public FilterMethodAopMaker(String pattern,MethodInterceptor interceptor) {
		filter = new PatternClassAndMethodFilter(pattern);
		this.interceptor = interceptor;
	}
	
	
	

	@Override
	public void makeAops(Class<?> targetClass, Method method, List<MethodInterceptor> interceptors) {
		if(filter.accept(targetClass) && filter.accept(method)) {
			interceptors.add(interceptor);
		}
	}

}
