package com.jzoom.zoom.ioc.impl;

import java.lang.reflect.Method;
import java.util.List;

import org.jzoom.zoom.common.Destroyable;
import org.jzoom.zoom.common.Initable;

import com.jzoom.zoom.common.res.MethodFilter;
import com.jzoom.zoom.common.utils.CachedClasses;
import com.jzoom.zoom.common.utils.Classes;
import com.jzoom.zoom.ioc.IocClass;
import com.jzoom.zoom.ioc.IocContainer;
import com.jzoom.zoom.ioc.IocInjector;

public class LazyIocClass extends SimpleIocClass {
	
	private String destroy;
	
	public LazyIocClass(Class<?> type, IocConstructor constructor,String destroy) {
		super(type, constructor,null,null);
		this.destroy = destroy;
	}

	@Override
	public void inject(Object target, IocContainer ioc) {
		if(this.getInjectors() == null) {
			synchronized (this) {
				List<IocInjector> injectors = IocUtils.createInjectors(target.getClass());
				this.setIocInjectors(injectors.toArray(new IocInjector[injectors.size()]));
				
				if(destroy!=null) {
					setDestroy( IocUtils.createIocDestroy(target.getClass(), destroy) );
				}
			}
		}
		
		super.inject(target, ioc);
	}

}
