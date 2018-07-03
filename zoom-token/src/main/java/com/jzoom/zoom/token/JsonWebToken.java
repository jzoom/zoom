package com.jzoom.zoom.token;

/**
 * json web token
 * @author jzoom
 *
 */
public interface JsonWebToken<T> {
	/**
	 * 编码
	 * @param data
	 * @return
	 */
	String encode(T data);
	
	/**
	 * 解码
	 * @param data
	 * @return
	 */
	T decode(String data);
	
}
