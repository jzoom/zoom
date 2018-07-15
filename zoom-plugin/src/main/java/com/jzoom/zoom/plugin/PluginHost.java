package com.jzoom.zoom.plugin;

import com.jzoom.zoom.common.res.ResScanner;

/**
 * 宿主程序
 * @author jzoom
 *
 */
public interface PluginHost {
	/**
	 * 
	 * @param event
	 * @param sender
	 * @param data
	 */
	void update(String event,String sender,Object data) throws NotSupportException;

	
	
}
