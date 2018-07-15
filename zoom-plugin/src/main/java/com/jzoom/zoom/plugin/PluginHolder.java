package com.jzoom.zoom.plugin;

import java.net.URL;

import com.jzoom.zoom.common.res.ClassResolvers;
import com.jzoom.zoom.plugin.PluginHost;
import com.jzoom.zoom.plugin.PluginLoadException;

public interface PluginHolder {

	void load( ) throws PluginLoadException ;
	
	void startup(PluginHost host,ClassResolvers resolvers) throws Exception;

	boolean isInstalled();

	void shutdown(PluginHost host);

	boolean isActivated();

	String getUid();

	URL getUrl();
	
}
