package com.jzoom.zoom.ioc.impl;

import com.jzoom.zoom.ioc.IocContainer;

class ReflectIocClassContructor implements IocConstructor {
	
	private Class<?> type;
	
	public ReflectIocClassContructor(Class<?> type) {
		this.type = type;
	}

	@Override
	public Object newInstance(IocContainer ioc) {
		try {
			return type.newInstance();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public Class<?> getType() {
		return type;
	}

}
