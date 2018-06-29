package com.jzoom.zoom.ioc.impl;

import java.lang.reflect.Field;

import com.jzoom.zoom.caster.Caster;
import com.jzoom.zoom.ioc.IocContainer;
import com.jzoom.zoom.ioc.IocInjector;
import com.jzoom.zoom.ioc.value.IocValue;

class ReflectFieldInjector implements IocInjector {
	
	private Field field;
	private IocValue iocValue;
	
	public  ReflectFieldInjector(Field field,IocValue iocValue) {
		this.field = field;
		this.iocValue = iocValue;
		this.field.setAccessible(true);
	}
	

	@Override
	public void inject(Object target, IocContainer ioc) {
		Object value = iocValue.get(ioc);
		try {
			field.set(target, value);
		} catch (Exception e) {
			//是否可以挽救?
			if(target!=null) {
				//如果能转化一下?
				try {
					Object castedValue = Caster.to(value, field.getType());
					field.set(target, castedValue);
					return;
				}catch (Exception ex) {
					//没救了
				}
			}
			throw new RuntimeException(String.format("设置字段值发生异常:对象%s 字段%s 值%s", target,field,value),e);
		} 
	}

}
