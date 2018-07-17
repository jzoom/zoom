package com.jzoom.zoom.ioc.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.jzoom.zoom.common.Destroyable;
import com.jzoom.zoom.ioc.IocDestroy;
import com.jzoom.zoom.ioc.IocObject;

public class GlobalObject implements IocObject {
	
	private static final Log log = LogFactory.getLog(GlobalObject.class);
	
	private IocDestroy destroy;
	private Object data;
	
	
	public GlobalObject() {
		
	}
	public GlobalObject(Object data) {
		this.data = data;
	}

	@Override
	public Object get() {
		return data;
	}

	@Override
	public void set(Object object) {
		this.data = object;
	}
	
	@Override
	public void destroy() {
		
		if(data != null) {
			if(log.isDebugEnabled()) {
				log.debug("正在销毁"+data.getClass());
			}
			if(destroy!=null) {
				destroy.destroy(data);
				
			}else {
				if(data instanceof Destroyable) {
					((Destroyable)data).destroy();
				}
			}
			data = null;
		}
		
		destroy = null;
	}
	@Override
	public void setDestroy(IocDestroy destroy) {
		this.destroy = destroy;
	}
	

}
