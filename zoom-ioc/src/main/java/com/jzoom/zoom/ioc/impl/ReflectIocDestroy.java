package com.jzoom.zoom.ioc.impl;

import java.lang.reflect.Method;

import com.jzoom.zoom.ioc.IocDestroy;

public class ReflectIocDestroy implements IocDestroy {
	
	
	private Method method;
	


	public ReflectIocDestroy(Method method ) {
		this.method = method;
	}

	@Override
	public void destroy(Object target) {
		try {
			method.invoke(target);
		} catch (Exception e) {
			//静悄悄
			e.printStackTrace();
		}
		
	}

	
}
