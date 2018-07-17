package com.jzoom.zoom.ioc;

import com.jzoom.zoom.common.Destroyable;

/**
 * ioc 容器
 * 
 * @author jzoom
 *
 */
public interface IocContainer extends Destroyable {

	/**
	 * 容器对象的生命周期
	 * @author jzoom
	 *
	 */
	public static interface IocContainerListener{
		void onCreated(Object target);
		void onDestroyed(Object tartet);
	}

	/**
	 * 全局范围
	 */
	public static final int APP = 0;

	/**
	 * request范围
	 */
	public static final int REQUEST = 1;

	/**
	 * session范围
	 */
	public static final int SESSION = 2;

	/**
	 * token范围
	 * 
	 */
	public static final int TOKEN = 3;

	/**
	 * 每次取出都是要new一个出来
	 */
	public static final int NEW = 4;

	/**
	 * 直接为某个id注册一个类
	 * 
	 * @param name
	 * @param clazz
	 */
	void register(String name, Class<?> clazz);

	/**
	 * 直接为某个接口注册一个类
	 * 
	 * @param interfaceClass
	 * @param clazz
	 */
	void register(Class<?> interfaceClass, Class<?> clazz);

	/**
	 * 直接注册一个对象
	 * 
	 * @param name
	 *            Class#getName 或者 id
	 * @param value
	 */
	void register(String name, Object value);

	/**
	 * 注入目标对象
	 * 
	 * @param target
	 */
	void inject(Object target);

	/**
	 * 释放scope对象
	 */
	void release(int scope);

	/**
	 * 对于非接口类型，直接用get就可以获取
	 * 
	 * @param classOfT
	 * @return
	 */
	<T> T get(Class<T> classOfT);

	/**
	 * 对于接口，如果需要直接用名称获取，事先需要在ioc容器注册
	 * 
	 * @param name
	 * @param classOfT
	 * @return
	 */
	<T> T get(String name);
	
	void addListener( IocContainerListener listener );

}
