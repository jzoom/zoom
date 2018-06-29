package com.jzoom.zoom.ioc.value;

import com.jzoom.zoom.common.config.ConfigReader;
import com.jzoom.zoom.ioc.IocContainer;

class IocConfigValue implements IocValue {

	private String name;

	public IocConfigValue(String name) {
		this.name = name;
	}

	@Override
	public Object get(IocContainer ioc) {
		return ConfigReader.getDefault().get(name);
	}

}
