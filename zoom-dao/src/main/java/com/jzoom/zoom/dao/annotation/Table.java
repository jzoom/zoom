package com.jzoom.zoom.dao.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.jzoom.zoom.dao.alias.AliasPolicyMaker;
import com.jzoom.zoom.dao.alias.impl.DetectPrefixAliasPolicyMaker;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Table {	
	/**
	 * 只需要指定这个对应到哪张表，其他由系统来判断吧
	 * @return
	 */
	String name();
	
	/**
	 * 默认情况下检测前缀
	 * @return
	 */
	Class<? extends AliasPolicyMaker> aliasMaker() default DetectPrefixAliasPolicyMaker.class;
}
