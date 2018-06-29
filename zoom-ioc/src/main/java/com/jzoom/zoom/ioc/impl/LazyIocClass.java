package com.jzoom.zoom.ioc.impl;

import java.util.List;

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
