package com.jzoom.zoom.ioc;

import java.lang.reflect.Method;

import com.jzoom.zoom.ioc.annonation.IocBean;

public interface IocClassFactory {

	
	
	/**
	 * 注册依赖关系
	 * @param moduleClass
	 * @param method
	 */
	void registerIocBean( Class<?> moduleClass, Method method );
	
	/**
	 * 通过名字注册一个IocBean
	 * @param name
	 */
	void registerIocBean(IocBean bean,Class<?> moduleClass, Method method);
	
	/**
	 * 直接注册一个依赖
	 * @param type
	 */
	IocClass registerType( Class<?> type );
	

	/**
	 * 根据注册的依赖，查找classOfT对应的IocClass
	 * @param classOfT
	 * @return
	 */
	 IocClass get( Class<?> classOfT );
	
	/**
	 * 根据注册的依赖，查找名称对应的IocClass
	 * @param name
	 * @return
	 */
	 IocClass get( String name );
}
