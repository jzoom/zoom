package com.jzoom.zoom.common.utils;

import java.util.HashMap;
import java.util.Map;

public class MapUtils {

	/**
	 * 方便的创建一个Map,使用方法: MapUtils.asMap(key0,value0,key1,value1......)
	 * @param values
	 * @return
	 */
	public static Map<String, Object> asMap( Object...values ){
		
		if(values.length % 2 != 0) {
			throw new RuntimeException("参数个数必须为2的倍数");
		}
		Map<String, Object> data = new HashMap<String, Object>();
		for(int i=0 ,c = values.length; i < c; i+=2) {
			data.put( (String) values[i], values[i+1]);
		}
		
		return data;
		
	}
	
}
