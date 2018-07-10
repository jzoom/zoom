package com.jzoom.zoom.web.rendering.impl;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.beetl.core.Configuration;
import org.beetl.core.GroupTemplate;
import org.beetl.core.ResourceLoader;
import org.beetl.core.Template;
import org.beetl.core.resource.StringTemplateResourceLoader;
import org.beetl.core.resource.WebAppResourceLoader;

public class BeetlRendering extends TemplateRendering {
	
	public static BeetlRendering createStringRendering() {
		
		StringTemplateResourceLoader resourceLoader = new StringTemplateResourceLoader();
		try {
			return new BeetlRendering(resourceLoader,Configuration.defaultConfiguration());
		}catch (IOException e) {
			throw new RuntimeException("创建BeetlRendering失败", e);
		}
		
	}
	
	public static BeetlRendering createFileRendering( ) {
		/*File path = PathUtils.getWebInfPath("");
		String key = ConfigReader.getDefault().getString("template.path");
		if(key == null) {
			//在WEB-INF的templates下面
			path = new File(path,"templates");
		}else {
			path = PathUtils.resolve(key);
		}*/
		File path = TemplateRendering.getDefaultPath();
		return createFileRendering(path);
	}
	
	private static BeetlRendering createFileRendering( File root ) {
		ResourceLoader resourceLoader = new WebAppResourceLoader(root.getAbsolutePath());
		try {
			return new BeetlRendering(resourceLoader,Configuration.defaultConfiguration());
		} catch (IOException e) {
			throw new RuntimeException("创建BeetlRendering失败", e);
		}
	}

	public GroupTemplate group;

	public BeetlRendering(ResourceLoader loader,Configuration cfg) {
		group = new GroupTemplate(loader, cfg);
	}

	@Override
	protected void render(HttpServletRequest request, HttpServletResponse response, String path,
			Map<String, Object> data) throws Exception {

		Template template = group.getTemplate(path + getExt());
		template.binding(data);
		template.renderTo(response.getOutputStream());
	}

}
