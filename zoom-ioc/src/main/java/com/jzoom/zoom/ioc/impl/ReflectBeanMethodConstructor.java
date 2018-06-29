package com.jzoom.zoom.ioc.impl;

import java.lang.reflect.Method;

import org.apache.commons.lang3.StringUtils;

import com.jzoom.zoom.ioc.IocContainer;
import com.jzoom.zoom.ioc.value.IocValue;

class ReflectBeanMethodConstructor implements IocConstructor {
	
	private String name;
	private Method method;
	private IocValue[] iocValues;

	public ReflectBeanMethodConstructor(String name, Method method, IocValue[] iocValues) {
		this.name = name;
		this.method = method;
		this.iocValues = iocValues;
	}

	@Override
	public Object newInstance(IocContainer ioc) {
		Object[] args = IocUtils.iocValues2Values(iocValues, ioc);
		try {
			return method.invoke(ioc.get(name), args);
		} catch (Exception e) {
			throw new RuntimeException(String.format("方法执行失败 %s 参数：%s",method , StringUtils.join(args,",")  ),e);
		} 
	}

	@Override
	public Class<?> getType() {
		return method.getReturnType();
	}

}
