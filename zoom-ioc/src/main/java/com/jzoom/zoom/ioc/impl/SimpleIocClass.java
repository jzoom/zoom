package com.jzoom.zoom.ioc.impl;

import javax.security.auth.Destroyable;

import org.jzoom.zoom.common.Initable;

import com.jzoom.zoom.common.utils.Classes;
import com.jzoom.zoom.ioc.IocClass;
import com.jzoom.zoom.ioc.IocContainer;
import com.jzoom.zoom.ioc.IocDestroy;
import com.jzoom.zoom.ioc.IocInjector;
import com.jzoom.zoom.ioc.IocObject;

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
