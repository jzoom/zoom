package com.jzoom.zoom.ioc.value;

import com.jzoom.zoom.ioc.IocContainer;

class IocEnvValue implements IocValue {

	private String name;
	
	public IocEnvValue(String name) {
		this.name = name;
	}

	@Override
	public Object get(IocContainer ioc) {
		return System.getenv(name);
	}

}
