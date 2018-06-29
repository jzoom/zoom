package com.jzoom.zoom.ioc.impl;

import com.jzoom.zoom.ioc.IocContainer;

interface IocField {

	/**
	 * 注入方法，由 Ioc 调用
	 * @param target
	 * @param ioc
	 */
	void set( Object target , IocContainer ioc );
	
}
