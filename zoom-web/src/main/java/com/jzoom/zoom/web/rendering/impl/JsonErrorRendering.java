package com.jzoom.zoom.web.rendering.impl;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.jzoom.zoom.common.utils.Classes;
import com.jzoom.zoom.common.utils.MapUtils;
import com.jzoom.zoom.web.action.ActionContext;
import com.jzoom.zoom.web.rendering.Rendering;
import com.jzoom.zoom.web.utils.ResponseUtils;

public class JsonErrorRendering implements Rendering {
	
	private static final Log log = LogFactory.getLog(JsonErrorRendering.class);

	@Override
	public boolean render(ActionContext context) throws Exception {
		HttpServletResponse response = context.getResponse();
		Object result = context.getRenderObject();
		if(result instanceof Exception) {
			Throwable exception = context.getException();
			exception = Classes.getCause(exception);
			response.setStatus(500);
			ResponseUtils.json(response, MapUtils.asMap("code", exception.getClass().getName(), "error",exception.getMessage()  )  );
		}else {
			ResponseUtils.json(response, result);
		}
		
		return true;
	}

}
