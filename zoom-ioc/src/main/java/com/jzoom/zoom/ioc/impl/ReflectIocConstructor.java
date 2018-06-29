package com.jzoom.zoom.ioc.impl;

import java.lang.reflect.Constructor;

import com.jzoom.zoom.ioc.IocContainer;
import com.jzoom.zoom.ioc.value.IocValue;

class ReflectIocConstructor implements IocConstructor {
	
	
	private Constructor<?> constructor;
	
	private IocValue[] args;
	
	public ReflectIocConstructor(Constructor<?> constructor,IocValue[] args) {
		this.constructor = constructor;
		this.constructor.setAccessible(true);
		this.args = args;
	}

	@Override
	public Object newInstance(IocContainer ioc) {
		
		try {
			return constructor.newInstance(IocUtils.iocValues2Values(args, ioc));
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public Class<?> getType() {
		return constructor.getDeclaringClass();
	}

}
