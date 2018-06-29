package com.jzoom.zoom.ioc.impl;

import com.jzoom.zoom.ioc.IocContainer;

/**
 * ioc构造器
 * @author jzoom
 *
 */
interface IocConstructor {
	
	
	Object newInstance(IocContainer ioc);
	
	Class<?> getType();
}
