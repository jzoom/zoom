package com.jzoom.zoom.ioc.impl;

import com.jzoom.zoom.ioc.IocContainer;

public interface IocMethod {

	/**
	 * 
	 * @param target
	 * @param ioc
	 */
	Object invoke( Object target,IocContainer ioc );
}
