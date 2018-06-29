package com.jzoom.zoom.ioc.impl;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang3.StringUtils;

import com.jzoom.zoom.ioc.ClassEnhance;
import com.jzoom.zoom.ioc.IocClass;
import com.jzoom.zoom.ioc.IocClassFactory;
import com.jzoom.zoom.ioc.IocDestroy;
import com.jzoom.zoom.ioc.IocInjector;
import com.jzoom.zoom.ioc.annonation.IocBean;

public class SimpleIocClassFactory implements IocClassFactory {
	
	private Map<String, IocClass> pool = new ConcurrentHashMap<String, IocClass>();
	private ClassEnhance enhance;
	
	public SimpleIocClassFactory(ClassEnhance enhance) {
		this.enhance = enhance;
	}

	@Override
	public void registerIocBean(Class<?> moduleClass, Method method) {
		registerIocBean(null, moduleClass, method);
	}

	@Override
	public void registerIocBean(IocBean bean, Class<?> moduleClass, Method method) {
		String name = StringUtils.isEmpty(bean.name()) ? null : bean.name();
		String destroy = StringUtils.isEmpty(bean.destroy()) ? null : bean.destroy();
		String init =StringUtils.isEmpty( bean.init() ) ? null : bean.init();
		String key = name != null ? name : method.getReturnType().getName();
		IocConstructor constructor = new ReflectMethodConstructor( moduleClass, method ,IocUtils.getIocValuesFromMethod(method)  );
		
		pool.put(key, new LazyIocClass( method.getReturnType(), constructor,destroy));
	}

	@Override
	public IocClass registerType(Class<?> type) {
		Class<?> enhancedType = enhance.enhance(type);
		IocConstructor constructor = IocUtils.createConstructorFromClass(enhancedType);
		List<IocInjector> list = IocUtils.createInjectors(type);
		//IocUtils.createIocDestroy(type, name);
		SimpleIocClass simpleIocClass = new SimpleIocClass( type, constructor,null,list.toArray(new IocInjector[list.size()]) );
		pool.put(type.getName(),simpleIocClass );
		return simpleIocClass;
	}

	
	@Override
	public IocClass get(Class<?> classOfT) {
		IocClass result = pool.get(classOfT.getName());
		if(result == null) {
			synchronized (this) {
				result = registerType(classOfT);
			}
		}
		return result;
	}

	@Override
	public IocClass get(String name) {
		return pool.get(name);
	}

}
