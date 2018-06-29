package com.jzoom.zoom.ioc.value;

import com.jzoom.zoom.ioc.IocContainer;

public class IocRawValue implements IocValue {
	
	private Object value;

	public IocRawValue(Object value) {
		this.value = value;
	}

	@Override
	public Object get(IocContainer ioc) {
		return value;
	}

}
