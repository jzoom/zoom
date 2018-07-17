package com.jzoom.zoom.aop.impl;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.jzoom.zoom.aop.AopFactory;
import com.jzoom.zoom.aop.AopMaker;
import com.jzoom.zoom.aop.MethodCaller;
import com.jzoom.zoom.aop.MethodInterceptor;
import com.jzoom.zoom.aop.maker.FilterMethodAopMaker;
import com.jzoom.zoom.common.Destroyable;
import com.jzoom.zoom.common.utils.Classes;
import com.jzoom.zoom.common.utils.OrderedList;

public abstract class AbsAopFactory implements AopFactory,Destroyable {
	public static final String CONFIG_FIELD_NAME = "_$configs";
	/**
	 * 所有增强类名称都以这个未结尾
	 */
	public static final String TAIL = "$Enhance";
	
	/**
	 * 
	 */
	private AopMaker[] aopMakers;
	private List<Class<?>> createdClasses;
	private List<AopConfig> createdConfigs;
	
	/**
	 * 这里暂时只允许静态
	 */
	private Map<Class<?>, Class<?>> enhanceMap;
	
	private List<AopConfig> configs;
	
	/**
	 * list
	 */
	private OrderedList<AopMaker> list;
	
	public AbsAopFactory(AopMaker...makers) {
		/**
		 * 定义一些内置的AopMater
		 */
		list = new OrderedList<AopMaker>();
		list.addAll(makers);
		
		configs = new ArrayList<AbsAopFactory.AopConfig>();
		enhanceMap = new HashMap<Class<?>, Class<?>>();
		
		this.createdClasses = new ArrayList<Class<?>>();
		this.createdConfigs = new ArrayList<AbsAopFactory.AopConfig>();
		
		
	}
	
	/**
	 * 销毁
	 */
	public void destroy() {
		if(enhanceMap != null) {
			Classes.destroy(enhanceMap);
			enhanceMap = null;
		}
		
		if(createdConfigs!=null) {
			Classes.destroy(this.createdConfigs);
			createdConfigs = null;
		}
		
		if(createdClasses!=null) {
			for (Class<?> clazz : createdClasses) {
				try {
					Classes.set(clazz,CONFIG_FIELD_NAME,null);
				} catch (Exception e) {
					
				}
			}
			createdClasses.clear();
			createdClasses = null;
		}
		
		if(list!=null) {
			Classes.destroy(list);
			list = null;
		}
		
		if(aopMakers != null) {
			Classes.destroy( aopMakers );
			aopMakers = null;
		}
		
		if(configs!=null) {
			Classes.destroy(configs);
			configs = null;
		}
		
	}
	
	private void build() {
		this.aopMakers = list.toArray(  new AopMaker[ list.size() ]  );
	}
	
	public AopFactory addMaker(AopMaker maker,int order) {
		list.add(maker,order);
		this.aopMakers = null;
		return this;
	}
	
	
	public AopFactory addFilter(MethodInterceptor interceptor,String pattern,int order) {
		addMaker(new FilterMethodAopMaker(pattern, interceptor), order);
		return this;
	}
	
	public static class AopConfig implements Destroyable{
		private MethodInterceptor[] interceptors;
		private Method method;
		
		private MethodCaller caller;
		
		public AopConfig(Method method,List<MethodInterceptor> interceptors) {
			this.method = method;
			this.interceptors = interceptors.toArray(new MethodInterceptor[interceptors.size()]);
		}

		public MethodInterceptor[] getInterceptors() {
			return interceptors;
		}


		public Method getMethod() {
			return method;
		}

		public MethodCaller getCaller() {
			return caller;
		}

		public void setCaller(MethodCaller caller) {
			this.caller = caller;
		}

		@Override
		public void destroy() {
			if(this.caller != null) {
				if(this.caller instanceof Destroyable) {
					((Destroyable)this.caller).destroy();
				}
				this.caller = null;
			}
			
			Classes.destroy(interceptors);
		}

	}

	

	

	@Override
	public Class<?> enhance(Class<?> targetClass)  {
		assert(targetClass!=null);
		
		Class<?> enhanced = enhanceMap.get(targetClass);
		if(enhanced!=null) {
			return enhanced;
		}
		
		if(targetClass.getName().endsWith(TAIL)) {
			return targetClass;
		}
		
		try {
			return Class.forName(targetClass.getName()+TAIL,false,targetClass.getClassLoader());
		}catch (ClassNotFoundException e) {
			
		}
		
		List<Method> methods = Classes.getPublicMethods(targetClass);
		if(methods.size() > 0) {
			
			List<AopConfig> configs = new ArrayList<AopConfig>();
			List<MethodInterceptor> interceptors = new ArrayList<MethodInterceptor>();
			
			if(aopMakers==null) {
				build();
			}
			
			for (Method method : methods) {
				interceptors.clear();
				
				for (AopMaker maker : aopMakers) {
					maker.makeAops( targetClass, method, interceptors);
				}
				
				if(interceptors.size() > 0) {
					configs.add(new AopConfig(method,interceptors));
				}
				
			}
			
			if(configs.size() > 0 ) {
				try {
					enhanced= enhance(targetClass,configs.toArray(new AopConfig[configs.size()]));
					
					createdConfigs.addAll(configs);
					createdClasses.add(targetClass);
					
				} catch (Exception e) {
					throw new RuntimeException("增强类失败",e);
				}
				enhanceMap.put(targetClass, enhanced);
				this.configs.addAll(configs);
				return enhanced;
			}
			
		}
		
		
		return targetClass;
	}
	
	


	protected abstract Class<?> enhance(Class<?> src, AopConfig[] aopConfigs) throws Exception;

}
