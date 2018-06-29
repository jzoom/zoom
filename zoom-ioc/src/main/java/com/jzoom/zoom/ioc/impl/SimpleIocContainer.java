package com.jzoom.zoom.ioc.impl;

import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.jzoom.zoom.common.Destroyable;

import com.jzoom.zoom.ioc.ClassEnhance;
import com.jzoom.zoom.ioc.IocClass;
import com.jzoom.zoom.ioc.IocClassFactory;
import com.jzoom.zoom.ioc.IocContainer;
import com.jzoom.zoom.ioc.IocDestroy;
import com.jzoom.zoom.ioc.IocObject;

public class SimpleIocContainer implements IocContainer {
	private Map<String, IocObject> objectPool;
	private IocClassFactory factory;
	
	
	public SimpleIocContainer(ClassEnhance enhance) {
		this(new SimpleIocClassFactory(enhance));
	}
	
	public SimpleIocContainer(IocClassFactory factory) {
		this.objectPool = new ConcurrentHashMap<String, IocObject>();
		this.factory = factory;
		this.objectPool.put(IocContainer.class.getName(), new GlobalObject(this)  );
		this.objectPool.put(IocClassFactory.class.getName(),new GlobalObject( factory ));
	}
	
	
	private Object getBean(Class<?> classOfT,String name,String key) {
		IocObject obj = objectPool.get(key);
		if(obj==null) {
			synchronized (objectPool) {
				IocClass proxy =classOfT == null? factory.get(name): factory.get(classOfT);
				Object bean = proxy.newInstance(this);
				obj = addObject(key,bean);
				proxy.inject(bean, this);
				obj.setDestroy(proxy.getDestroy());
			}
		}
		return  obj.get();
	}
	
	
	private GlobalObject addObject(String key,Object obj) {
		GlobalObject object = new GlobalObject(obj);
		objectPool.put(key, object);
		for (IocContainerListener iocContainerListener : listeners) {
			iocContainerListener.onCreated(obj);
		}
		return object;
	}
	
	@Override
	public void register(String name, Class<?> clazz) {
		getBean(clazz, null, name);
	}

	@Override
	public void register(Class<?> interfaceClass, Class<?> clazz) {
		assert(interfaceClass!=null && clazz!=null);
		register(interfaceClass.getName(), clazz);
	}

	@Override
	public void register(String name, Object value) {
		addObject(name, value);
	}

	@Override
	public void inject(Object target) {
		assert(target!=null);
		IocClass proxy = factory.get(target.getClass());
		proxy.inject(target, this);
	}

	@Override
	public void release(int scope) {
		// TODO Auto-generated method stub
		
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T get(Class<T> classOfT) {
		assert(classOfT!=null);
		return (T) getBean(classOfT, null,classOfT.getName());
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T get(String name) {
		assert(name!=null);
		return (T) getBean(null,name,name);
	}
	
	private Set<IocContainerListener> listeners = Collections.synchronizedSet(new HashSet<IocContainer.IocContainerListener>());

	@Override
	public void addListener(IocContainerListener listener) {
		listeners.add(listener);
		
	}

	@Override
	public void destroy() {
		listeners.clear();
		for (Entry<String, IocObject> entry : objectPool.entrySet()) {
			IocObject obj = entry.getValue();
			if(obj.get() == this) {
				obj.set(null);
				continue;
			}
			
			obj.destroy();
		}
		objectPool.clear();
	}

}
