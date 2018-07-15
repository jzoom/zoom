package com.jzoom.zoom.plugin;

import java.util.Map;

public interface Plugin {
	
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
