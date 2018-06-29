package com.jzoom.zoom.web.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 
 * 
 * @author jzoom
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD,ElementType.PARAMETER})
public @interface Param {
	
	/**
	 * 如果参数的名称是这个值，表示的是RequestBody
	 */
	public static final String BODY = "@";

	/**
	 * 
	 * @return
	 */
	String name() default "";

	/**
	 * 注释
	 * @return
	 */
	String comment() default "";
	
	
	
	
}
