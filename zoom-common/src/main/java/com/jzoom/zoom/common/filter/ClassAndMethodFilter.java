package com.jzoom.zoom.common.filter;

import java.lang.reflect.Method;

public interface ClassAndMethodFilter {
	boolean accept(Class<?> clazz) ;
	boolean accept(Method method) ;
}
