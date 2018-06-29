package com.jzoom.zoom.ioc;

/**
 * ioc 注入类，使用本接口对某个实例进行注入
 * @author jzoom
 *
 */
public interface IocInjector {
	
	/** 
	 * 对一个对象进行注入
	 * @param target
	 * @param ioc
	 */
	void inject(Object target,IocContainer ioc);
}
