package com.jzoom.zoom.ioc;


import org.jzoom.zoom.common.Destroyable;
import org.jzoom.zoom.common.Initable;

import com.jzoom.zoom.ioc.impl.IocMethod;

/**
 * ioc 对象工厂，使用本接口创建的对象自动完成依赖注入
 * @author jzoom
 *
 */
public interface IocClass{
	
	/**
	 * 获取对象的类
	 * @return
	 */
	Class<?> getType();

	
	/**
	 * 创建一个新的对象，
	 * @return
	 */
	Object newInstance(IocContainer ioc);
	
	/**
	 * 对target进行注入
	 * @param target
	 */
	void inject(Object target,IocContainer ioc);

	/**
	 * 获取本对象的销毁接口
	 * @return
	 */
	IocDestroy getDestroy();
	
}
