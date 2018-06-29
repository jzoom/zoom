package com.jzoom.zoom.ioc.impl;

import java.lang.reflect.Method;

import com.jzoom.zoom.ioc.IocContainer;
import com.jzoom.zoom.ioc.IocInjector;
import com.jzoom.zoom.ioc.value.IocValue;

class ReflectMethodInjector implements IocInjector {
	
	IocValue[] args;
	Method method;
	
	
	public ReflectMethodInjector(Method method,IocValue[] args) {
		this.method = method;
		this.args = args;
	}
	

	@Override
	public void inject(Object target, IocContainer ioc) {
		try {
			method.invoke(target ,IocUtils.iocValues2Values(args,ioc) );
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

}
