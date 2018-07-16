package com.jzoom.zoom.web.configuration;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang3.StringUtils;

import com.jzoom.zoom.caster.Caster;
import com.jzoom.zoom.caster.Caster.CasterProvider;
import com.jzoom.zoom.caster.ValueCaster;
import com.jzoom.zoom.common.filter.impl.AnnotationFilter;
import com.jzoom.zoom.common.filter.pattern.PatternFilterFactory;
import com.jzoom.zoom.common.res.ClassResolver;
import com.jzoom.zoom.common.utils.Classes;
import com.jzoom.zoom.ioc.IocClassFactory;
import com.jzoom.zoom.ioc.IocContainer;
import com.jzoom.zoom.ioc.annonation.Inject;
import com.jzoom.zoom.ioc.annonation.IocBean;
import com.jzoom.zoom.ioc.annonation.Module;
import com.jzoom.zoom.web.utils.RequestUtils;

public class SimpleConfigBuilder extends ClassResolver {
	
	@Inject
	private IocContainer ioc;
	private Class<?> clazz;
	
	private List<Class<?>> list;
	
	@Inject
	private IocClassFactory classFactory;
	
	public SimpleConfigBuilder() {
		setClassNameFilter(PatternFilterFactory.createFilter("*.modules.*"));
		setClassFilter( new AnnotationFilter<Class<?>>( Module.class )  );
		
		list = new ArrayList<Class<?>>();
	}
	
	/**
	 * 对caster进行配置，增加参数解析的部分
	 */
	@Inject
	public void configCaster() {
		Caster.register(HttpServletRequest.class, Map.class, new Request2Map());
		Caster.registerCastProvider(new Map2BeanProvider());
	//	Caster.registerCastProvider(new Map2BeanProvider());
		
	}
	private static class Map2Bean implements ValueCaster{
		private Class<?> toType;
		public Map2Bean(Class<?> toType) {
			this.toType = toType;
		}

		@SuppressWarnings({ "unchecked", "rawtypes" })
		@Override
		public Object to(Object src) {
			Map data = (Map)src;
			try {
				Object result = toType.newInstance();
				BeanUtils.populate(result, data);
				return result;
			}catch (Exception e) {
				throw new Caster.CasterException(e);
			}
			
		}
		
	}
	static class Request2BeanProvider implements CasterProvider{

		@Override
		public ValueCaster getCaster(Class<?> srcType, Class<?> toType) {
			if(!srcType.isAssignableFrom(HttpServletRequest.class)) {
				return null;
			}
			return null;
		}
		
	}

	static class Map2BeanProvider implements CasterProvider{

		@Override
		public ValueCaster getCaster(Class<?> srcType, Class<?> toType) {
			if(Map.class.isAssignableFrom(srcType)) {
				if(Classes.isSimple(toType)) {
					//转化简单类型应该是不行的
					return null;
				}
				//java开头的一律略过
				if(toType.getName().startsWith("java"))return null;
				
				return new Map2Bean(toType);
			}
			
			return null;
			
		}
		
	}
	
	static class Request2Map implements ValueCaster{

		@Override
		public Object to(Object src) {
			HttpServletRequest request = (HttpServletRequest)src;
			return RequestUtils.getParameters( request );
		}
		
	}
	
	
	
	@Override
	public void visitClass(Class<?> clazz) {
		this.clazz = clazz;
		list.add(clazz);
	}
	
	@Override
	public void clear() {
		
	}

	
	@Override
	public void visitMethod(Method method) {
		IocBean bean = method.getAnnotation(IocBean.class);
		if(bean != null) {
			classFactory.registerIocBean(bean,clazz, method);
		}
	}

	@Override
	public boolean resolveFields() {
		return false;
	}


	@Override
	public boolean resolveMethods() {
		return true;
	}

	
	@Override
	public void end() {
		
		for (Class<?> type : list) {
			log.info(String.format( "初始化Module [%s]" ,type));
			ioc.get(type);
		}
		
		for (Class<?> type : list) {
			//bean
			List<Method> methods = Classes.getPublicMethods(type);
			for (Method method : methods) {
				IocBean bean = method.getAnnotation(IocBean.class);
				if(bean != null) {
					String key = StringUtils.isEmpty(bean.name()) ? method.getReturnType().getName() : bean.name();
					log.info(String.format("正在初始化 %s", key));
				
				//	IocMethod iocMethod = IocUtils.createIocMethod(method);
			//		iocMethod.invoke(module, ioc);
					ioc.get(key);
				}
			}
		}
		

		list.clear();
		
	}
}
