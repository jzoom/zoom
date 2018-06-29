package com.jzoom.zoom.aop;

import java.lang.reflect.Method;
import java.util.List;

/**
 * 对方法创建 {@link MethodInterceptor}
 * @see com.jzoom.zoom.aop.impl.AbsAopFactory
 * @author jzoom
 *
 */
public interface AopMaker {
	/**
	 * aop增强实际上是针对method增强
	 * @param targetClass								目标class
	 * @param method										目标method
	 * @param interceptors								这个数组将保存创建的AopMaker
	 */
	void makeAops(Class<?> targetClass, Method method,List<MethodInterceptor> interceptors);
}
