package com.jzoom.zoom.dao.alias.impl;

import com.jzoom.zoom.dao.alias.AliasPolicy;

public class AutoAliasPolicy implements AliasPolicy {
	/**
	 * 根据实际名称获取别名
	 * @param column
	 * @return
	 */
	public String getAlias(String column) {
		return null;
	}
	
	/**
	 * 根据别名获取到实际字段名称
	 * @param alias
	 * @return
	 */
	public String getNameByAlias(String alias) {
		return null;
	}
}
