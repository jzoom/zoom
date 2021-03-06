package com.jzoom.zoom.web.parameter.parser.impl;

import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.jzoom.zoom.caster.Caster;
import com.jzoom.zoom.common.utils.Classes;
import com.jzoom.zoom.web.parameter.adapter.ParameterAdapter;
import com.jzoom.zoom.web.parameter.adapter.impl.form.NamedFormParameterAdapter;
import com.jzoom.zoom.web.parameter.adapter.impl.form.RequestBodyForm2BeanAdapter;
import com.jzoom.zoom.web.parameter.adapter.impl.form.RequestBodyForm2MapAdapter;
import com.jzoom.zoom.web.parameter.adapter.impl.map.PathMapParameterAdapter;

public class FormParameterParserFactory extends AbsParameterParserFactory<HttpServletRequest> {

	

	@Override
	public void destroy() {
		// TODO Auto-generated method stub
		
	}
	
	@SuppressWarnings("unchecked")
	@Override
	protected ParameterAdapter<HttpServletRequest> createAdapter(String name, Class<?> type, Type genericType, Annotation[] annotations) {
		if (isRequestBody(name, annotations)) {
			if( type == Map.class ) {
				return RequestBodyForm2MapAdapter.ADAPTER;
			}
			return RequestBodyForm2BeanAdapter.ADAPTER;
		} else if(isPathVariable(name, annotations)){
			
			return (ParameterAdapter)PathMapParameterAdapter.ADAPTER;
			
		} else {
			// 简单类型直接来
			if (Classes.isSimple(type)) {
				return NamedFormParameterAdapter.ADAPTER;
			} else if (type.isArray()) {

			} else if (Collection.class.isAssignableFrom(type)) {
				
			} else if (Map.class.isAssignableFrom(type)) {

			} else {
				// 按照bean处理

			}
		}

		return null;
	}


}
