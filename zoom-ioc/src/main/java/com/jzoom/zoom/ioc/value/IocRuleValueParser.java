package com.jzoom.zoom.ioc.value;

abstract class IocRuleValueParser implements IocValueParser{
	
	private String prefix;
	
	public IocRuleValueParser(String prefix) {
		this.prefix = prefix;
	}

	public String getPrefix() {
		return prefix;
	}

	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}

}
