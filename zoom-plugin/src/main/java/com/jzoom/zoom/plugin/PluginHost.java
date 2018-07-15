package com.jzoom.zoom.plugin;

import java.net.URL;

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

	PluginHolder getPluginById(String id);
	
	PluginHolder getPluginByUrl(URL plugin) ;
	
	PluginHolder load(URL url)  throws PluginLoadException;
	
	void startup() throws PluginStartupExeption;
}
