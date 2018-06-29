package com.jzoom.zoom.ioc;

import java.util.List;
import java.util.Map;

/**
 * ioc 配置类
 * @author jzoom
 *
 */
public interface IocSetting {
	
	/**
	 * bean名称
	 * @return
	 */
	String getName();
	
	/**
	 * class名称   如： com.xxx.aaa.BClass
	 * 
	 * @return
	 */
	String getClassName();
	
	/**
	 * 
	 * 1、类名#方法		如：  com.my.XXFactory#create		
	 * 2、bean名称#方法	如  	 mybean#create
	 * @return
	 */
	String getFactory();
	
	
	/**
	 * 上述构造方法需要调用的值  
	 * 
	 * @return
	 */
	List<Object> getArgs();
	
	/**
	 * 获取到注入字段
	 * @return
	 */
	Map<String, Object> getFields();
	
	/**
	 * 获取到注入方法
	 * @return
	 */
	Map<String, Object[]> getMethods();

}
