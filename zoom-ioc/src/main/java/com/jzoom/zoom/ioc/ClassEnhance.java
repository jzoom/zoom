package com.jzoom.zoom.ioc;

public interface ClassEnhance {
	/**
	 * 增强类的功能，产生一个新的类
	 * @param src
	 * @return
	 */
	Class<?> enhance(Class<?> src);
}
