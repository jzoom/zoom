package com.jzoom.zoom.ioc.impl;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.jzoom.zoom.common.res.MethodFilter;
import com.jzoom.zoom.common.utils.CachedClasses;
import com.jzoom.zoom.common.utils.Classes;
import com.jzoom.zoom.ioc.IocContainer;
import com.jzoom.zoom.ioc.IocDestroy;
import com.jzoom.zoom.ioc.IocInjector;
import com.jzoom.zoom.ioc.IocSetting;
import com.jzoom.zoom.ioc.annonation.Inject;
import com.jzoom.zoom.ioc.value.IocClassValue;
import com.jzoom.zoom.ioc.value.IocRefValue;
import com.jzoom.zoom.ioc.value.IocValue;

public class IocUtils {
	
	/**
	 * 创建一个针对Method的注入器
	 * @param method
	 * @return
	 */
	public static IocInjector createMethodInjector(Method method) {
		Annotation[][] methodAnnotations = method.getParameterAnnotations();
		Class<?>[] types = method.getParameterTypes();
		return createMethodInjector(method, createArgs(methodAnnotations,types));
	}
	
	public static IocMethod createIocMethod(Method method) {
		Annotation[][] methodAnnotations = method.getParameterAnnotations();
		Class<?>[] types = method.getParameterTypes();
		return createIocMethod(method, createArgs(methodAnnotations,types));
	}
	


	private static final Object[] EMPTY = new Object[] {};
	public static Object[] iocValues2Values(IocValue[] args,IocContainer ioc) {
		if(args.length==0)return EMPTY;
		int c = args.length;
		Object[] result = new Object[c];
		for(int i=0; i < c; ++i) {
			result[i] = args[i].get(ioc);
		}
		return result;
	}
	
	
	
	static IocInjector createMethodInjector(Method method,IocValue[] args) {
		return new ReflectMethodInjector(method, args);
	}
	private static IocMethod createIocMethod(Method method, IocValue[] args) {
		return new ReflectIocMethod(method, args);
	}
	
	static IocValue[] createArgs(Annotation[][] methodAnnotations, Class<?>[] types) {
		int index = 0;
		IocValue[] args = new IocValue[types.length];
		IocValue arg;
		for (Class<?> type : types) {
			Inject paramInject = null;
			Annotation[] annotations = methodAnnotations[index];
			for (Annotation annotation : annotations) {
				if (annotation instanceof Inject) {
					paramInject = (Inject) annotation;
				}
			}
			if (paramInject != null && !StringUtils.isEmpty(paramInject.value())) {
				arg = new IocRefValue(paramInject.value());
			} else {
				arg = new IocClassValue(type);
			}
			args[index++] = arg;
		}
		return args;
	}

	/**
	 * 从Method解析iocValue
	 * @param method
	 * @return
	 */
	public static IocValue[] getIocValuesFromMethod(Method method) {
		Class<?>[] types = method.getParameterTypes();
		Annotation[][] annotations = method.getParameterAnnotations();
		IocValue[] args = IocUtils.createArgs(annotations, types);
		return args;
	}
	
	/**
	 * 从constructor解析IocValue
	 * @param constructor
	 * @return
	 */
	public static IocValue[] getIocValuesFromConstructor(Constructor<?> constructor) {
		Class<?>[] types = constructor.getParameterTypes();
		Annotation[][] annotations = constructor.getParameterAnnotations();
		IocValue[] args = IocUtils.createArgs(annotations, types);
		return args;
	}
	
	
	
	public static IocConstructor createConstructorFromClass(Class<?> clazz) {
		// 查找参数列表
		Constructor<?>[] constructors;
		try {
			constructors = clazz.getConstructors();
		} catch (Throwable e) {
			throw new RuntimeException(String.format("获取[%s]的构造函数发生错误", clazz), e);
		}
		if (constructors.length == 0) {
			return new ReflectIocClassContructor(clazz);
		} else if (constructors.length == 1) {
			// 查看参数列表，并初始化
			Constructor<?> constructor = constructors[0];
			constructor.setAccessible(true);
			Class<?>[] types = constructor.getParameterTypes();
			Annotation[][] annotations = constructor.getParameterAnnotations();
			IocValue[] args = IocUtils.createArgs(annotations, types);
			return new ReflectIocConstructor(constructor, args);
		} else {
			//找到默认构造函数
			for (Constructor<?> constructor : constructors) {
				if(constructor.getParameterTypes().length==0) {
					return new ReflectIocClassContructor(clazz);
				}
			}
			throw new RuntimeException(String.format("构造函数数量太多,找不到合适的构造函数:%s", clazz));
		}
	}
	
	private static IocValue createValue(Class<?> fieldClass,String name,Field field) {
		
		if (!StringUtils.isEmpty(name)) {
			if(name.startsWith("cfg:")) {
				//配置
				return new IocConfigValue(name.substring("cfg:".length()));
			}
			
			//简单类型的inject永远从ConfigReader来
			if(Classes.isSimple(fieldClass)) {
				//简单类型的inject永远从ConfigReader来
				return new IocConfigValue(name);
			}else {
				return new IocRefValue(name);
			}
			
		} else {
		
			if(Classes.isSimple(fieldClass)) {
				//简单类型?,难道就是field的名称?
				return new IocConfigValue(field.getName());
			}else {
				return  new IocClassValue(fieldClass);
			}
		}
	}
	
	public static List<IocInjector> createInjectors(Class<?> clazz) {
		List<Field> fields = Classes.getFields(clazz);
		List<Method> methods = Classes.getPublicMethods(clazz);
		List<IocInjector> injectors = new ArrayList<IocInjector>();
		
		
		for (Field field : fields) {
			Inject inject = field.getAnnotation(Inject.class);
			if (inject != null) {
				Class<?> fieldClass = field.getType();
				String name = inject.value();
				
				injectors.add(createFieldInjector(field,createValue(fieldClass, name, field)));
			}
		}

		for (Method method : methods) {
			Inject inject = method.getAnnotation(Inject.class);
			if (inject != null) {
				injectors.add( IocUtils.createMethodInjector(method) );
			}
		}
		
		
		return injectors;
	}

	/**
	 * 查找{@link com.jzoom.zoom.ioc.annonation.IocBean}标注的{@link com.jzoom.zoom.ioc.annonation.IocBean#destroy}函数,并创建{@link IocDestroy}
	 * @param clazz
	 * @param name
	 * @return
	 */
	public static IocDestroy createIocDestroy( Class<?> clazz, final String name ) {
		assert(!clazz.isInterface());
		Method method = CachedClasses.fetchPublicMethod(clazz, new MethodFilter() {
			
			@Override
			public boolean accept(Method value) {
			
				return value.getName().equals(name) && value.getParameterTypes().length == 0;
			}
		});
		
		return new ReflectIocDestroy(method);
	}

	private static IocInjector createFieldInjector(Field field, IocValue value) {
		return new ReflectFieldInjector(field, value);
	}
}
