package com.jzoom.zoom.pluginloader.impl;

import java.io.InputStream;
import java.net.URL;
import java.net.URLClassLoader;

import com.jzoom.zoom.common.io.Io;
import com.jzoom.zoom.common.res.ClassResolvers;
import com.jzoom.zoom.common.res.ResScanner;
import com.jzoom.zoom.plugin.Plugin;
import com.jzoom.zoom.plugin.PluginHost;
import com.jzoom.zoom.plugin.PluginLoadException;
import com.jzoom.zoom.pluginloader.PluginHolder;

public class SimplePluginHolder implements PluginHolder {
	
	private URL url;
	private URLClassLoader classLoader;
	private Plugin plugin;

	public SimplePluginHolder(URL url) {
		this.url = url;
		classLoader = new URLClassLoader(new URL[] { url });
	}

	@Override
	public void load(  ) throws PluginLoadException {
		try {
			Class<?> clazz = Class.forName("com.jzoom.zoom.plugin.MainPlugin",false,classLoader);
			plugin = (Plugin) clazz.newInstance();
		}catch (Exception e) {
			throw new PluginLoadException(e);
		}
	}



	@Override
	public boolean isInstalled() {
		return true;
	}

	@Override
	public void startup(PluginHost host,ClassResolvers resolvers) throws Exception {
		InputStream is = null;
		try {
			is = url.openStream();
			ResScanner scanner = new ResScanner();
			scanner.scan(is,classLoader);
			resolvers.visit(scanner);
			
			
			plugin.startup(host);
			
		}finally {
			Io.close(is);
		}
		
		
		
	}

	@Override
	public void shutdown(PluginHost host) {
		if(plugin!=null) {
			plugin.shutdown(host);
		}
	}

}
