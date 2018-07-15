package com.jzoom.zoom.admin.controllers;

import java.net.MalformedURLException;
import java.net.URL;

import com.jzoom.zoom.admin.models.AdminException;
import com.jzoom.zoom.admin.models.PluginDao;
import com.jzoom.zoom.dao.Dao;
import com.jzoom.zoom.dao.Record;
import com.jzoom.zoom.ioc.annonation.Inject;
import com.jzoom.zoom.plugin.PluginHolder;
import com.jzoom.zoom.plugin.PluginHost;
import com.jzoom.zoom.plugin.PluginLoadException;
import com.jzoom.zoom.plugin.PluginStartupExeption;
import com.jzoom.zoom.web.annotation.Controller;
import com.jzoom.zoom.web.annotation.JsonResponse;
import com.jzoom.zoom.web.annotation.Mapping;
import com.jzoom.zoom.web.annotation.Param;

@Controller(key = "plugin")
public class PluginController extends BaseDaoController<PluginDao> {

	@Inject("admin")
	private Dao dao;

	@Inject
	private PluginHost host;

	@Mapping(value = "install/{id}", method = Mapping.POST)
	@JsonResponse
	public void install(@Param(name = "{id}") String id) throws AdminException {
		Record record = dao.table("sys_plugin").where("id", id).fetch();
		if (record == null) {
			throw new AdminException("找不到本插件");
		}
		try {
			URL url = new URL(record.getString("uri"));
			
			PluginHolder pluginHolder = host.getPluginByUrl(url);
			if(pluginHolder==null) {
				pluginHolder = host.load( url);
			}
			
			host.startup();
			
			
		} catch (MalformedURLException e) {
			throw new AdminException("插件url不合法");
		} catch (PluginLoadException e) {
			e.printStackTrace();
			throw new AdminException("加载插件失败");
		} catch (PluginStartupExeption e) {
			throw new AdminException("加载插件失败");
		}
		
		
	}

	@Mapping(value = "active/{id}", method = Mapping.POST)
	@JsonResponse
	public void active(@Param(name = "{id}") String id) throws AdminException {
		Record record = dao.table("sys_plugin").where("id", id).fetch();
		if (record == null) {
			throw new AdminException("找不到本插件");
		}

	}

}
