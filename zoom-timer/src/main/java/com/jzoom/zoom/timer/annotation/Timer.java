package com.jzoom.zoom.timer.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 对一个Method标注，表示一个定时任务
 * @author randyren
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Timer {
	
	/**
	 * cron表达式, 可以指定配置 ${config_name}
	 * @return
	 */
	String value();
	
}
