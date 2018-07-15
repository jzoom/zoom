package com.jzoom.zoom.pluginloader.impl;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.jzoom.zoom.common.res.ClassResolvers;
import com.jzoom.zoom.plugin.NotSupportException;
import com.jzoom.zoom.plugin.PluginHolder;
import com.jzoom.zoom.plugin.PluginHost;
import com.jzoom.zoom.plugin.PluginLoadException;
import com.jzoom.zoom.plugin.PluginStartupExeption;

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

	public PluginHolder load(URL plugin) throws PluginLoadException {
		PluginHolder holder = new SimplePluginHolder(plugin);
		holder.load();
		holders.add(holder);
		return holder;
	}

	public PluginHolder getPluginById(String id) {
		for (PluginHolder pluginHolder : holders) {
			if(id.equals(pluginHolder.getUid())){
				return pluginHolder;
			}
		}
		return null;
	}

	public PluginHolder getPluginByUrl(URL plugin) {
		assert(plugin!=null);
		for (PluginHolder pluginHolder : holders) {
			if(plugin.equals(pluginHolder.getUrl())){
				return pluginHolder;
			}
		}
		return null;
	}

	public void startup() throws PluginStartupExeption {
		List<Throwable> list = new ArrayList<Throwable>();
		for (PluginHolder pluginHolder : holders) {
			if (pluginHolder.isInstalled() && !pluginHolder.isActivated()) {
				try {
					pluginHolder.startup(this, resolvers);
					runningPlugins.add(pluginHolder);
				} catch (Throwable e) {
					logger.error("Startup plugin fail!", e);
					list.add(e);
				}
			}
		}

	}

	public void shutdown() {
		for (PluginHolder plugin : runningPlugins) {
			try {
				plugin.shutdown(this);
			} catch (Throwable e) {

			}

		}
	}

	@Override
	public void update(String event, String sender, Object data) throws NotSupportException {

	}

}
