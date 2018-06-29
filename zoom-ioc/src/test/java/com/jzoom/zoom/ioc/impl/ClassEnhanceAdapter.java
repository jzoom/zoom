package com.jzoom.zoom.ioc.impl;

import com.jzoom.zoom.aop.AopFactory;
import com.jzoom.zoom.aop.AopMaker;
import com.jzoom.zoom.aop.javassist.SimpleAopFactory;
import com.jzoom.zoom.ioc.ClassEnhance;

public class ClassEnhanceAdapter implements ClassEnhance {
	
	private AopFactory factory;
	public ClassEnhanceAdapter(ClassLoader classLoader,AopMaker...makers) {
		factory = new SimpleAopFactory(classLoader,makers);
	}
	public ClassEnhanceAdapter(AopMaker...makers) {
		factory = new SimpleAopFactory(makers);
	}

	@Override
	public Class<?> enhance(Class<?> src) {
		return factory.enhance(src);
	}

}
