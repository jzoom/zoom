package com.jzoom.zoom.web.action.impl;

import java.lang.reflect.Method;

import com.jzoom.zoom.aop.impl.ReflectMethodCaller;
import com.jzoom.zoom.common.ConfigurationConstants;
import com.jzoom.zoom.ioc.IocContainer;
import com.jzoom.zoom.ioc.annonation.Inject;
import com.jzoom.zoom.reflect.ClassInfo;
import com.jzoom.zoom.web.action.Action;
import com.jzoom.zoom.web.action.ActionFactory;
import com.jzoom.zoom.web.action.ActionInterceptorFactory;
import com.jzoom.zoom.web.parameter.ParameterParser;
import com.jzoom.zoom.web.parameter.ParameterParserFactory;
import com.jzoom.zoom.web.parameter.PreParameterParser;
import com.jzoom.zoom.web.parameter.PreParameterParserFactory;
import com.jzoom.zoom.web.parameter.parser.impl.SimpleParameterParserFactory;
import com.jzoom.zoom.web.parameter.pre.impl.SimplePreParameterParserFactory;
import com.jzoom.zoom.web.rendering.Rendering;
import com.jzoom.zoom.web.rendering.RenderingFactory;
import com.jzoom.zoom.web.rendering.impl.SimpleErrorRenderingFactory;
import com.jzoom.zoom.web.rendering.impl.SimpleRenderingFactory;

public class SimpleActionFactory implements ActionFactory {
	
	@Inject
	protected IocContainer ioc;
	@Inject
	protected ClassInfo classInfo;
	@Inject( ConfigurationConstants.SERVER_ENCODING )
	protected String encoding;
	
	private ParameterParserFactory parameterParserFactory;
	private RenderingFactory renderingFactory;
	private RenderingFactory errorRenderingFactory;
	private PreParameterParserFactory preParameterParserFactory;

	public SimpleActionFactory() {
		
	}

	protected Rendering createRendering(Class<?> targetClass, Method method) {
		return getRenderingFactory().createRendering(targetClass, method);
	}

	protected ParameterParser createParameterParser(Class<?> controllerClass, Method method) {
		String[] names = classInfo.getParameterNames(controllerClass, method);
		return getParameterParserFactory().createParamParser(controllerClass, method, names);
	}

	protected PreParameterParser createPreParameterParser(Class<?> controllerClass, Method method) {
		return getPreParameterParserFactory().createPreParameterParser(controllerClass, method);
	}

	protected Rendering createErrorRendering(Class<?> controllerClass, Method method) {
		return getErrorRenderingFactory().createRendering(controllerClass, method);
	}

	@Override
	public Action createAction(Object target, Class<?> controllerClass, Method method,
			ActionInterceptorFactory actionInterceptorFactory) {
		if(encoding == null) {
			encoding = "utf-8";
		}
		Action action = new Action();
		action.setEncoding(encoding);
		action.setIoc(getIoc());
		action.setMethod(method);
		action.setCaller(new ReflectMethodCaller(method));
		action.setTarget(target);

		action.setActionInterceptors(actionInterceptorFactory.create(controllerClass, method));
		action.setPreParamParser(createPreParameterParser(controllerClass, method));
		action.setParamParser(createParameterParser(controllerClass, method));
		action.setRendering(createRendering(controllerClass, method));
		action.setErrorRendering(createErrorRendering(controllerClass,method));

		return action;
	}

	public IocContainer getIoc() {
		return ioc;
	}

	public ParameterParserFactory getParameterParserFactory() {
		if(parameterParserFactory==null) {
			parameterParserFactory = new SimpleParameterParserFactory();
		}
		return parameterParserFactory;
	}

	public void setParameterParserFactory(ParameterParserFactory parameterParserFactory) {
		this.parameterParserFactory = parameterParserFactory;
	}

	public RenderingFactory getRenderingFactory() {
		if(errorRenderingFactory==null) {
			renderingFactory = new SimpleRenderingFactory();
		}
		return renderingFactory;
	}

	public void setRenderingFactory(RenderingFactory renderingFactory) {
		this.renderingFactory = renderingFactory;
	}

	public RenderingFactory getErrorRenderingFactory() {
		if(errorRenderingFactory==null)
			errorRenderingFactory = new SimpleErrorRenderingFactory();
		return errorRenderingFactory;
	}

	public void setErrorRenderingFactory(RenderingFactory errorRenderingFactory) {
		this.errorRenderingFactory = errorRenderingFactory;
	}

	public PreParameterParserFactory getPreParameterParserFactory() {
		if(preParameterParserFactory==null)
			preParameterParserFactory = new SimplePreParameterParserFactory();
		return preParameterParserFactory;
	}

	public void setPreParameterParserFactory(PreParameterParserFactory preParameterParserFactory) {
		this.preParameterParserFactory = preParameterParserFactory;
	}

}
