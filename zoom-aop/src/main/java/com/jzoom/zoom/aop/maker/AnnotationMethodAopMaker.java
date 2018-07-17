package com.jzoom.zoom.aop.maker;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.List;

import com.jzoom.zoom.aop.AopMaker;
import com.jzoom.zoom.aop.MethodInterceptor;
import com.jzoom.zoom.common.Destroyable;
import com.jzoom.zoom.common.utils.Classes;

public abstract class AnnotationMethodAopMaker<T extends Annotation> implements AopMaker ,Destroyable {

	
	private Class<T> annotationClass;
	
	
	@SuppressWarnings("unchecked")
	public AnnotationMethodAopMaker(){
		//获取泛型类型
		annotationClass = (Class<T>) Classes.getTypeParams(getClass())[0];
	}
	@Override
	public void destroy() {
		annotationClass = null;
	}
	@Override
	public void makeAops( Class<?> tartetClass, Method method, List<MethodInterceptor> interceptors) {
		int modifier = method.getModifiers();
		if(Modifier.isFinal(modifier) || Modifier.isPrivate(modifier)){
			return;
		}
		T annotation = method.getAnnotation(annotationClass);
		if(annotation!=null){
			makeAops(annotation,method,interceptors);
		}
	}

	protected abstract void makeAops(T annotation, Method method, List<MethodInterceptor> interceptors);
	

}
