package com.jzoom.zoom.pluginloader;

import com.jzoom.zoom.common.res.ClassResolvers;
import com.jzoom.zoom.plugin.PluginHost;
import com.jzoom.zoom.plugin.PluginLoadException;

public interface PluginHolder {

	void load( ) throws PluginLoadException ;
	
	void startup(PluginHost host,ClassResolvers resolvers) throws Exception;

	boolean isInstalled();

	void shutdown(PluginHost host);
	
}
