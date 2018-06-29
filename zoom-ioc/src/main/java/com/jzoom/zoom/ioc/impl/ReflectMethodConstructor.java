package com.jzoom.zoom.ioc.impl;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import com.jzoom.zoom.ioc.IocContainer;
import com.jzoom.zoom.ioc.value.IocValue;

public class ReflectMethodConstructor implements IocConstructor {
	private Method method;
	private IocValue[] values;
	private Class<?> moduleClass;

	
	public ReflectMethodConstructor( Class<?> moduleClass,Method method,IocValue[] values ) {
		this.method = method;
		this.values = values;
		this.moduleClass = moduleClass;
		
	}

	@Override
	public Object newInstance(IocContainer ioc) {
		try {
			return method.invoke( ioc.get(moduleClass) ,  IocUtils.iocValues2Values(values, ioc)   );
		} catch (Exception e) {
			throw new RuntimeException(e);
		} 
	}

	@Override
	public Class<?> getType() {
		return method.getReturnType();
	}

}
