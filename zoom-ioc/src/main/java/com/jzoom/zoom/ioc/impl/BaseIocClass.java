package com.jzoom.zoom.ioc.impl;



import org.jzoom.zoom.common.Destroyable;
import org.jzoom.zoom.common.Initable;

import com.jzoom.zoom.ioc.IocClass;
import com.jzoom.zoom.ioc.IocContainer;
import com.jzoom.zoom.ioc.IocDestroy;

public abstract class BaseIocClass implements IocClass {
	
	private Class<?> type;
	private IocConstructor constructor;
	private IocDestroy destroy;
	
	public BaseIocClass(Class<?> type,IocConstructor constructor,IocDestroy destroy) {
		this.type = type;
		this.constructor = constructor;
		this.destroy = destroy;
	}

	@Override
	public Class<?> getType() {
		return type;
	}

	
	@Override
	public Object newInstance(IocContainer ioc) {
		return constructor.newInstance(ioc);
	}

	public void setType(Class<?> type) {
		this.type = type;
	}

	public IocDestroy getDestroy() {
		return destroy;
	}

	public void setDestroy(IocDestroy destroy) {
		this.destroy = destroy;
	}



	
}
