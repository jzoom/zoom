package com.jzoom.zoom.ioc.impl;

import com.jzoom.zoom.common.config.ConfigReader;
import com.jzoom.zoom.ioc.IocContainer;
import com.jzoom.zoom.ioc.value.IocValue;

public class IocConfigValue implements IocValue {
	private String name;
	

	public IocConfigValue(String name) {
		this.name = name;
	}

	@Override
	public Object get(IocContainer ioc) {
		return ConfigReader.getDefault().get(name);
	}

}
