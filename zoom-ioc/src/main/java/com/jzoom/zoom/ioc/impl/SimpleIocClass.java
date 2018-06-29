package com.jzoom.zoom.ioc.impl;

import com.jzoom.zoom.ioc.IocContainer;
import com.jzoom.zoom.ioc.IocDestroy;
import com.jzoom.zoom.ioc.IocInjector;

public class SimpleIocClass extends BaseIocClass {
	
	public SimpleIocClass(Class<?> type, IocConstructor constructor , IocDestroy destroy, IocInjector[] injectors) {
		super(type, constructor,destroy);
		this.injectors = injectors;
	}

	
	private IocInjector[] injectors;

	@Override
	public void inject(Object target,IocContainer ioc) {
		for (IocInjector iocInjector : injectors) {
			iocInjector.inject(target, ioc);
		}
	}
	
	public void setIocInjectors(IocInjector[] injectors) {
		this.injectors = injectors;
	}

	public IocInjector[] getInjectors() {
		return injectors;
	}

	
	
}
