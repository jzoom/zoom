package com.jzoom.zoom.plugin;

import java.util.Map;

public interface Plugin {
	
	/**
	 * hook point
	 */
	public static final String ADD_MENU = "ADD_MENU";
	/**
	 * 
	 */
	public static final String REMOVE_MENU = "ADD_MENU";
	
	
	
	/**
	 * 名称
	 * @return
	 */
	String getUId();
	
	/**
	 * 获取插件信息（如版本等）
	 * @return
	 */
	Map<String, Object> getInfo();
	
	void shutdown(PluginHost host);
	
	void startup(PluginHost host) throws Exception;
	
	
}
