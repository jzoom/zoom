package com.jzoom.zoom.ioc.value;

import com.jzoom.zoom.ioc.IocContainer;

public class IocRefValue implements IocValue {

	private String name;

	public IocRefValue(String name) {
		this.name = name;
	}

	@Override
	public Object get(IocContainer ioc) {
		return ioc.get(name);
	}

}
