package com.jzoom.zoom.ioc.value;

import com.jzoom.zoom.ioc.IocContainer;

public class IocClassValue implements IocValue {
	private Class<?> type;
	
	
	public IocClassValue(Class<?> type) {
		this.type = type;
	}


	@Override
	public Object get(IocContainer ioc) {
		return ioc.get(type);
	}
	
}
