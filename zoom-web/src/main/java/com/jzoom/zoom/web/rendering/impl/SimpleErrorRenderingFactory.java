package com.jzoom.zoom.web.rendering.impl;

import java.lang.reflect.Method;

import com.jzoom.zoom.web.annotation.JsonResponse;
import com.jzoom.zoom.web.rendering.Rendering;
import com.jzoom.zoom.web.rendering.RenderingFactory;

public class SimpleErrorRenderingFactory implements RenderingFactory {
	
	private JsonErrorRendering jsonErrorRendering = new JsonErrorRendering();
	private TemplateRendering templateRendering;
	
	public SimpleErrorRenderingFactory(){
		templateRendering = BeetlRendering.createFileRendering();
	}

	@Override
	public Rendering createRendering(Class<?> targetClass, Method method) {
		
		return new GroupRendering(SimpleRenderingFactory.viewRendering,createRendering2(targetClass,method));
	}

	public Rendering createRendering2(Class<?> targetClass, Method method) {
		if(targetClass.isAnnotationPresent(JsonResponse.class) || method.isAnnotationPresent(JsonResponse.class)) {
			return jsonErrorRendering;
		}
		return templateRendering;
	}
}
