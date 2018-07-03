package com.jzoom.zoom.common.codec;

import org.apache.commons.codec.digest.DigestUtils;

public class HashStr {
	
	public static String md5(String str) {
		return DigestUtils.md5Hex(str);
	}
	
}
