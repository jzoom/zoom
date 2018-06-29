package com.jzoom.zoom.ioc.impl;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import com.jzoom.zoom.ioc.IocContainer;
import com.jzoom.zoom.ioc.value.IocValue;

public class ReflectIocMethod implements IocMethod {
	private Method method;
	private IocValue[] args;

	public ReflectIocMethod(Method method, IocValue[] args) {
		this.method = method;
		this.args = args;
	}

	@Override
	public Object invoke(Object target, IocContainer ioc) {
		
		try {
			return method.invoke(target, IocUtils.iocValues2Values(args, ioc));
		} catch (Exception e) {
			throw new RuntimeException(e);
		} 
	}

}
