package com.jzoom.zoom.web.view.impl;

import javax.servlet.http.HttpServletResponse;

import com.jzoom.zoom.common.json.JSON;
import com.jzoom.zoom.web.utils.ResponseUtils;
import com.jzoom.zoom.web.view.View;

/**
 * 渲染一个json
 * @author jzoom
 *
 */
public class JsonView implements View {

	private Object data;
	
	
	private int status;
	
	/**
	 * 需要转化的object
	 * @param data
	 */
	public JsonView(Object data) {
		this(200, data);
	}
	
	/**
	 * 自定义http status用这个构造函数
	 * @param status
	 * @param data
	 */
	public JsonView( int status, Object data) {
		this.status = status;
		this.data = data;
	}
	
	@Override
	public void render(HttpServletResponse response) throws Exception {
		response.setStatus(status);
		ResponseUtils.write(response, JSON.stringify(data));
	}

}
