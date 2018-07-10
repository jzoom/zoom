package com.jzoom.zoom.web.rendering.impl;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.jzoom.zoom.web.action.ActionContext;
import com.jzoom.zoom.web.exception.StatusException;
import com.jzoom.zoom.web.rendering.Rendering;
import com.jzoom.zoom.web.utils.PathUtils;
import com.jzoom.zoom.web.utils.RequestUtils;

/**
 * @author jzoom
 *
 */
public abstract class TemplateRendering implements Rendering {

	/**
	 * 模板后缀如.html
	 */
	private String ext;

	/**
	 * 获取默认模板位置
	 * 
	 * @return
	 */
	public static File getDefaultPath() {
		return PathUtils.getWebInfPath("").getParentFile();
	}

	static final Pattern pattern = Pattern.compile("([a-z]+)\\:([a-zA-Z0-9\\:\\/]+)");

	public String getExt() {

		return ext == null ? ".html" : ext;
	}

	public void setExt(String ext) {
		if (!ext.startsWith(".")) {
			ext = "." + ext;
		}
		this.ext = ext;
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean render(ActionContext context) throws Exception {
		Object result = context.getRenderObject();

		HttpServletResponse response = context.getResponse();
		if (result instanceof StatusException) {
			response.setStatus( ((StatusException)result).getStatus() );
		}
		HttpServletRequest request = context.getRequest();
		String path = context.getAction().getPath();
		Map<String, Object> data;
		if (result instanceof Map) {
			data = (Map<String, Object>) result;
		} else {
			data = null;
		}
		data = merge(data, context);
		render(request, response, path, data);
		return true;
	}

	private Map<String, Object> merge(Map<String, Object> data, ActionContext context) {

		if (context.getData() != null) {
			if (data == null) {
				data = new HashMap<String, Object>();
			}
			data.putAll(context.getData());
		}

		if (data == null) {
			data = new HashMap<String, Object>();
		}

		RequestUtils.merge(data, context.getRequest());

		return data;
	}

	/**
	 * 
	 * @param request
	 * @param response
	 * @param path
	 * @param data
	 */
	protected abstract void render(HttpServletRequest request, HttpServletResponse response, String path,
			Map<String, Object> data) throws Exception;

}
