package com.jzoom.zoom.aop.maker;

import java.lang.reflect.Method;
import java.util.List;

import com.jzoom.zoom.aop.AopMaker;
import com.jzoom.zoom.aop.MethodInterceptor;
import com.jzoom.zoom.common.filter.impl.ClassAndMethodFilter;

public class FilterMethodAopMaker implements AopMaker {
	

	private ClassAndMethodFilter filter;
	private MethodInterceptor interceptor;
	
	
	public FilterMethodAopMaker(String pattern,MethodInterceptor interceptor) {
		filter = new ClassAndMethodFilter(pattern);
		this.interceptor = interceptor;
	}
	
	
	

	@Override
	public void makeAops(Class<?> targetClass, Method method, List<MethodInterceptor> interceptors) {
		if(filter.accept(targetClass) && filter.accept(method)) {
			interceptors.add(interceptor);
		}
	}

}
