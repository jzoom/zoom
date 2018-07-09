package com.jzoom.zoom.web.parameter.parser.impl;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Type;

import javax.xml.bind.DatatypeConverter;

import org.jzoom.zoom.common.Destroyable;

import com.jzoom.zoom.web.annotation.Param;
import com.jzoom.zoom.web.parameter.ParameterParser;
import com.jzoom.zoom.web.parameter.ParameterParserFactory;
import com.jzoom.zoom.web.parameter.adapter.ParameterAdapter;
import com.jzoom.zoom.web.parameter.adapter.impl.BasicParameterAdapter;

public abstract class AbsParameterParserFactory<T> implements ParameterParserFactory,Destroyable {
	private static final EmptyParamterParser EMPTY = new EmptyParamterParser();

	
	public AbsParameterParserFactory( ) {
	}
	
	protected abstract ParameterAdapter<T> createAdapter(String name,Class<?> type, Type genericType, Annotation[] annotations );
	
	@SuppressWarnings("rawtypes")
	@Override
	public ParameterParser createParamParser(Class<?> controllerClass, Method method,String[] names) {
		

		int c= names.length;
		if(c>0) {
			Annotation[][] paramAnnotations = method.getParameterAnnotations();
			Class<?>[] types = method.getParameterTypes();
			Type[] genericTypes = method.getGenericParameterTypes();
			ParameterAdapter[] adapters = new ParameterAdapter[c];
			for(int i=0; i < c; ++i) {
				String name = names[i];
				Annotation[] annotations = paramAnnotations[i];
				Class<?> type = types[i];
				Type genericType = genericTypes[i];
				adapters[i] = getAdapter(name, type,  genericType, annotations);
			}
			return new DefaultParameterParser(names,types,adapters);
			
		}else {
			return EMPTY;
		}
	}
	
	
	protected ParameterAdapter<?> getAdapter(String name,Class<?> type, Type genericType, Annotation[] annotations){
		ParameterAdapter<?> adapter = BasicParameterAdapter.getAdapter(type);
		if(adapter!=null) {
			return adapter;
		}
		
		return createAdapter(name, type, genericType,annotations);
		
	}
	
	
	protected boolean isPathVariable(String name,Annotation[] annotations) {
		for (Annotation annotation : annotations) {
			if(annotation instanceof Param) {
				Param param = (Param) annotation;
				if( param.name().startsWith("{") && param.name().endsWith("}") ) {
					return true;
				}
			}
		}
		return false;
	}


	protected boolean isRequestBody(String name,Annotation[] annotations) {
		for (Annotation annotation : annotations) {
			if(annotation instanceof Param) {
				Param param = (Param) annotation;
				if( Param.BODY.equals(param.name()) ) {
					return true;
				}
			}
		}
		return false;
	}

}
