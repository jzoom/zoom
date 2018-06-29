package com.jzoom.zoom.ioc.impl;

import java.util.List;
import java.util.Map;

import com.jzoom.zoom.ioc.IocSetting;

class SimpleIocSetting implements IocSetting {

	private Map<String, Object> data;
	
	public SimpleIocSetting(Map<String, Object> map) {
		this.data = map;
	}

	@Override
	public String getName() {
		return (String) data.get("name");
	}

	@Override
	public String getClassName() {
		return (String) data.get("class");
	}

	@Override
	public String getFactory() {
		return (String) data.get("factory");
	}


	@SuppressWarnings("unchecked")
	@Override
	public Map<String, Object> getFields() {
		return (Map<String, Object>) data.get("fields");
	}

	@SuppressWarnings("unchecked")
	@Override
	public Map<String, Object[]> getMethods() {
		return (Map<String, Object[]>) data.get("methods");
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Object> getArgs() {
		return (List<Object>) data.get("args");
	}

}
