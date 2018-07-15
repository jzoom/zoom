package com.jzoom.zoom.pluginloader.impl;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.jzoom.zoom.common.res.ClassResolvers;
import com.jzoom.zoom.plugin.NotSupportException;
import com.jzoom.zoom.plugin.PluginHost;
import com.jzoom.zoom.plugin.PluginLoadException;
import com.jzoom.zoom.pluginloader.PluginHolder;

public class SimplePluginHost implements PluginHost {
	
	private List<PluginHolder> holders;
	
	private ClassResolvers resolvers;
	
	private static final Log logger = LogFactory.getLog(PluginHost.class);
	
	private List<PluginHolder> runningPlugins;
	
	public SimplePluginHost(ClassResolvers resolvers) {
		holders = new ArrayList<PluginHolder>();
		runningPlugins = new ArrayList<PluginHolder>();
		this.resolvers = resolvers;
	}
	
	public void load( URL plugin ) throws PluginLoadException {
		PluginHolder holder = new SimplePluginHolder( plugin );
		holder.load( );
		holders.add(holder);
	}
	
	
	
	public void startup() {
		for (PluginHolder pluginHolder : holders) {
			if(pluginHolder.isInstalled()) {
				try {
					pluginHolder.startup(this,resolvers);
					runningPlugins.add(pluginHolder);
				} catch (Throwable e) {
					logger.error("Startup plugin fail!",e);
				}
			}
		}
		
	}
	
	public void shutdown() {
		for (PluginHolder plugin : runningPlugins) {
			try {
				plugin.shutdown(this);
			}catch (Throwable e) {
				
			}
			
		}
	}

	@Override
	public void update(String event, String sender, Object data) throws NotSupportException {
		
	}

}
