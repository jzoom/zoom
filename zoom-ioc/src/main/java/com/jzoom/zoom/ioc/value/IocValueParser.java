package com.jzoom.zoom.ioc.value;

/**
 * 从一个原始值转成{@link IocValue}
 * @author jzoom
 *
 */
public interface IocValueParser {
	
	IocValuePair parse(Object value);

}
