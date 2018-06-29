package com.jzoom.zoom.common.el;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.codec.digest.DigestUtils;

import com.jzoom.zoom.common.config.ConfigReader;

public class ElParser {
	private static Pattern EL_PATTERN = Pattern.compile("\\$\\{([a-zA-Z0-9_]+)\\}");

	/**
	 * 解析配置   
	 * @param key  可以为字符串，可以为${config名称}
	 * @return
	 */
	public static String parseValue( String value) {
		Matcher matcher = EL_PATTERN.matcher(value);
		if(matcher.matches()) {
			value = matcher.group(1);
			return ConfigReader.getDefault().getString(value);
		}
		
	
	
		return value;
	}
}
