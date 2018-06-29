package com.jzoom.zoom.ioc.impl;

import java.lang.reflect.Method;

import com.jzoom.zoom.ioc.IocContainer;
import com.jzoom.zoom.ioc.value.IocValue;

class ReflectStaticMethodConstructor implements IocConstructor{
	
	private Method method;
	private IocValue[] args;
	
	public ReflectStaticMethodConstructor(Method method,IocValue[] args) {
		this.method = method;
		this.args = args;
	}
	

	@Override
	public Object newInstance(IocContainer ioc) {
		try {
			return method.invoke(null, IocUtils.iocValues2Values(args, ioc)  );
		} catch (Exception e) {
			throw new RuntimeException(String.format("调用静态方法%s发生异常", method));
		}
	}

	@Override
	public Class<?> getType() {
		return method.getReturnType();
	}

}
