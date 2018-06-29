package com.jzoom.zoom.web.utils;

import com.jzoom.zoom.ioc.IocContainer;

public class WebUtils {
	
	private static IocContainer ioc;

	public static void setIoc(IocContainer ioc) {
		WebUtils.ioc = ioc;
	}
	
	public static IocContainer getIoc() {
		return ioc;
	}
}
