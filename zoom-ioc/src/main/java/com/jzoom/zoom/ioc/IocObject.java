package com.jzoom.zoom.ioc;

/**
 * 对象的容器
 * @author jzoom
 *
 */
public interface IocObject {

	/**
	 * 获取对象
	 * @return
	 */
	Object get();
	
	/**
	 * 设置对象
	 * @param object
	 */
	void set(Object object);

	void destroy();
	
	void setDestroy(IocDestroy destroy);
}
