package com.jzoom.zoom.ioc.impl;

import com.jzoom.zoom.ioc.IocContainer;
import com.jzoom.zoom.ioc.IocInjector;

public class SimpleIocObjectProxy implements IocConstructor, IocInjector{
	
	private final IocInjector[] injectors;
	private final IocConstructor constructor;
	
	
	
	public SimpleIocObjectProxy(IocConstructor constructor,  IocInjector[] injectors) {
		this.injectors = injectors;
		this.constructor = constructor;
		
	}

	@Override
	public void inject(Object target, IocContainer ioc) {
		for (IocInjector iocInjector : injectors) {
			iocInjector.inject(target, ioc);
		}
	}

	@Override
	public Object newInstance(IocContainer ioc) {
		return constructor.newInstance(ioc);
	}

	public Class<?> getType() {
		return constructor.getType();
	}

}
