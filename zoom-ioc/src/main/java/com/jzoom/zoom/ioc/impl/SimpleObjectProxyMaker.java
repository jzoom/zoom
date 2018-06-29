package com.jzoom.zoom.ioc.impl;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.text.similarity.LevenshteinDistance;

import com.jzoom.zoom.common.utils.Classes;
import com.jzoom.zoom.ioc.ClassEnhance;
import com.jzoom.zoom.ioc.IocInjector;
import com.jzoom.zoom.ioc.IocSetting;
import com.jzoom.zoom.ioc.annonation.Inject;
import com.jzoom.zoom.ioc.value.IocClassValue;
import com.jzoom.zoom.ioc.value.IocRefValue;
import com.jzoom.zoom.ioc.value.IocValue;
import com.jzoom.zoom.ioc.value.IocValuePair;
import com.jzoom.zoom.ioc.value.IocValueParser;
import com.jzoom.zoom.ioc.value.SimpleValueParser;

/**
 * 
 * 
 * 内部存储的所有bean必须在程序启动的时候就注册好，并且全部都是全局变量，在应用程序期间不会改变
 * 
 * @author jzoom
 *
 */
public class SimpleObjectProxyMaker {

	private static final Log log = LogFactory.getLog(SimpleObjectProxyMaker.class);
	
	private ClassEnhance aopFactory;
	private IocSetting[] settings;
	private IocValueParser valueParser;
 
	public SimpleObjectProxyMaker(ClassEnhance aopFactory, IocSetting... settings) {
		this.aopFactory = aopFactory;
		this.settings = settings;
		this.valueParser = new SimpleValueParser(this);
	}


	private IocInjector createFieldInjector(Field field, IocValue value) {
		return new ReflectFieldInjector(field, value);
	}
	
	private IocConstructor createConstructor(Class<?> clazz) {
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
	
	private IocConstructor createConstructor(IocSetting setting) {
		
		if(!StringUtils.isEmpty(setting.getClassName())) {
			return createConstructor(setting.getClassName(),setting.getArgs());
		}
		
		if(!StringUtils.isEmpty(setting.getFactory())) {
			return createConstructorByFactory(setting.getFactory(), setting.getArgs());
		}
		
		throw new RuntimeException("不能通过配置");
	}

	public Class<?> getFactoryType(IocSetting setting){
		String factory = setting.getFactory();
		int indesOfSharp = factory.indexOf("#");
		if(indesOfSharp < 0 ) {
			throw new RuntimeException("factory参数的形式为:"
					+ "1、bean名称#bean方法, 如: mybean#create"
					+ "2、class名称#静态方法, 如: com.xx.XXFactory#create");
		}
		
		String prefix = factory.substring(0,indesOfSharp);
		String endfix = factory.substring(indesOfSharp+1);
		
		IocValuePair[] pairs = parseIocValuePairs(setting.getArgs());
		
		do {
			try {
				Class<?> classOfFactory = Class.forName(prefix);
				List<Method> methods = Classes.getStaticMethods(classOfFactory,endfix);
				if(methods.size() > 0) {
					//找到以后直接返回
					Method method = findMethod(pairs, methods);
					return method.getReturnType();
					//return new ReflectStaticMethodConstructor(classOfFactory, method);
				}
			}catch (ClassNotFoundException e) {
				
			}
			
		}while(false);
		//必须要同时满足找到对应的类，找到对应的方法才行
		//尝试查找bean和对应的method
		Class<?> clazz = getBeanType(prefix);
		List<Method> methods = Classes.getPublicMethods(clazz,endfix);
		Method method = findMethod(pairs, methods);
		return method.getReturnType();
	}
	private IocConstructor createConstructorByFactory(String factory, List<Object> args) {
		int indesOfSharp = factory.indexOf("#");
		if(indesOfSharp < 0 ) {
			throw new RuntimeException("factory参数的形式为:"
					+ "1、bean名称#bean方法, 如: mybean#create"
					+ "2、class名称#静态方法, 如: com.xx.XXFactory#create");
		}
		
		
		String prefix = factory.substring(0,indesOfSharp);
		String endfix = factory.substring(indesOfSharp+1);
		IocValuePair[] pairs = parseIocValuePairs(args);
		
		do {
			try {
				Class<?> classOfFactory = Class.forName(prefix);
				List<Method> methods = Classes.getStaticMethods(classOfFactory,endfix);
				if(methods.size() > 0) {
					//找到以后直接返回
					Method method = findMethod(pairs, methods);
					//return method.getReturnType();
					return new ReflectStaticMethodConstructor( method , extraIocValues(pairs))  ;
				}
			}catch (ClassNotFoundException e) {
				
			}
			
		}while(false);
		//必须要同时满足找到对应的类，找到对应的方法才行
		//尝试查找bean和对应的method
		Class<?> clazz = getBeanType(prefix);
		List<Method> methods = Classes.getPublicMethods(clazz,endfix);
		Method method = findMethod(pairs, methods);
		return new ReflectBeanMethodConstructor( prefix, method ,extraIocValues(pairs) );
	}
	
	private IocValuePair[] parseIocValuePairs( List<Object> list ) {
		if(list!=null && list.size() > 0) {
			IocValuePair[] values = new IocValuePair[list.size()];
			for (int i=0 , c = list.size(); i < c ; ++i) {
				values[i]=valueParser.parse( list.get(i) );
			}
			
			return values;
		}
		return new IocValuePair[] {};
	}

	private IocConstructor createConstructor(String className, List<Object> list) {
		try {
			Class<?> clazz = Class.forName(className);
			if(list!=null && list.size() > 0) {
				IocValuePair[] pairs = parseIocValuePairs(list);
				IocValue[] iocValues = extraIocValues(pairs);
				return new ReflectIocConstructor(findConstructor(pairs, clazz), iocValues);
			}
			return new ReflectIocClassContructor(clazz);
		}catch (ClassNotFoundException e) {
			throw new RuntimeException( "初始化Bean发生错误，找不到类"+className,e  );
		}
	}
	
	private IocValue[] extraIocValues(IocValuePair[] pairs) {
		int index = 0;
		IocValue[] iocValues = new IocValue[pairs.length];
		for (IocValuePair pair : pairs) {
			iocValues[index++] = pair.getValue();
		}
		return iocValues;
	}


	private boolean isFit(Class<?>[] expectMaybeTypes,Class<?>[] paramTypes) {
		
		for(int i=0; i < expectMaybeTypes.length; ++i) {
			
			if(!isFit(expectMaybeTypes[i],paramTypes[i])) {
				return false;
			}
			
		}
		
		return true;
	}
	
	public Class<?> getBeanType(String name){
		
		IocSetting setting = searchSettingByName(name);
		return getSettingType(setting);
	}
	
	/**
	 * 获取ioc 配置对应的类型
	 * @param setting
	 * @return
	 */
	public Class<?> getSettingType(IocSetting setting){
		
		if( setting.getClassName() != null ) {
			return getClass(setting.getClassName());
		}
		
		if(setting.getFactory() != null) {
			return getFactoryType(setting);
		}
		
		throw new RuntimeException("配置中定义的Bean，class和facotry必须定义其中一个");
	}
	
	
	
	


	private Class<?> getClass(String className){
		try {
			return Class.forName(className);
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(String.format("没有找到IocSetting中指定的类%s",className));
		}
	}

	private boolean isFit(Class<?> expectMaybeType, Class<?> paramType) {
		
		if(log.isTraceEnabled()) {
			log.trace( String.format(" 查看是不是匹配的类型 %s %s ",expectMaybeType,paramType ));
		}
		
		if(expectMaybeType == paramType || paramType.isAssignableFrom(expectMaybeType)) {
			return true;
		}
		
		if(expectMaybeType == String.class) {
			//只要是简单类型都行
			if(Classes.isSimple(paramType)) {
				return true;
			}
		}
		
		if( Classes.isNumber(expectMaybeType)  ) {
			//也必须是数字类型
			return Classes.isNumber(paramType);
		}
		
		if(Classes.isBoolean(expectMaybeType)) {
			return Classes.isBoolean(paramType);
		}
		
		//其他类型，看下是不是继承的
		
		
		return false;
	}
	
	private Class<?>[] extraTypes(IocValuePair[] pairs){
		Class<?>[] expectMaybeTypes = new Class<?>[pairs.length];
		int index = 0;
		for (IocValuePair pair : pairs) {
			expectMaybeTypes[index++] = pair.getType();
		}
		return expectMaybeTypes;
	}

	/**
	 * 从methods中找到类型与pairs匹配的Method
	 * @param pair
	 * @param methods
	 * @return
	 */
	private Method findMethod(IocValuePair[] pairs,List<Method> methods) {
		Class<?>[] expectMaybeTypes = extraTypes(pairs);
		for (Method method : methods) {
			Class<?>[] paramTypes = method.getParameterTypes();
			if(paramTypes.length == expectMaybeTypes.length && isFit(expectMaybeTypes, paramTypes)) {
				return method;
			}
		}
		throw new RuntimeException(String.format("找不到对应的方法, 参数类型列表:[%s]",StringUtils.join(expectMaybeTypes,",")));
	}

	private Constructor<?> findConstructor(IocValuePair[] pairs,Class<?> clazz){
		Constructor<?>[] constructors = clazz.getConstructors();
		Class<?>[] expectMaybeTypes = extraTypes(pairs);
	
		for (Constructor<?> constructor : constructors) {
			Class<?>[] paramTypes = constructor.getParameterTypes();
			if(paramTypes.length == expectMaybeTypes.length && isFit(expectMaybeTypes, paramTypes)) {
				return constructor;
			}
		}
		
		throw new RuntimeException(String.format("找不到对应的构造函数,类:%s 参数类型列表:[%s]",clazz,StringUtils.join(expectMaybeTypes,",")));
	}
	
	
	private IocSetting searchSettingByClassName(String className) {
		for (IocSetting iocSetting : settings) {
			if(className.equals(iocSetting.getClassName())) {
				return iocSetting;
			}
		}
		return null;
	}
	
	
	
	private IocSetting searchSettingByName(String name){
		for (IocSetting iocSetting : settings) {
			if(name.equals(iocSetting.getName())) {
				return iocSetting;
			}
		}
		
		//最小编辑距离查找
		for (IocSetting iocSetting : settings) {
			if(iocSetting.getName()!=null) {
				Integer min = LevenshteinDistance.getDefaultInstance().apply(name, iocSetting.getName());
				if( min < 3 ) {
					//还是手工确认下吧
					throw new RuntimeException(String.format("没有找到[%s]对应的bean，是不是[%s]?", name,iocSetting.getName()));
				}
			}
		}
		
		throw new RuntimeException(String.format("没有找到[%s]对应的bean",name));
	}



	
	private Map<String, SimpleIocObjectProxy> objectProxyMap = new ConcurrentHashMap<String, SimpleIocObjectProxy>();

	public SimpleIocObjectProxy getObjectProxy(Class<?> classOfT) {
		String key = classOfT.getName();
		SimpleIocObjectProxy proxy = objectProxyMap.get(key);
		if(proxy==null) {
			synchronized (objectProxyMap) {
				proxy = createObjectProxy(classOfT);
				objectProxyMap.put(key, proxy);
			}
		}
		return proxy;
	}
	
	public SimpleIocObjectProxy getObjectProxy(String name) {
		SimpleIocObjectProxy proxy =  objectProxyMap.get(name);
		if(proxy==null) {
			synchronized (objectProxyMap) {
				IocSetting setting = searchSettingByName(name);
				proxy = createObjectProxy(setting);
				objectProxyMap.put(name, proxy);
			}
		}
		return proxy;
	}
	
	private  SimpleIocObjectProxy createObjectProxy(IocSetting setting) {
		Class<?> clazz = getClass(setting.getClassName());
		return createObjectProxy(clazz, setting);
	}

	private List<IocInjector> createInjectors(Class<?> clazz, IocSetting setting) {
		List<Field> fields = Classes.getFields(clazz);
		List<Method> methods = Classes.getPublicMethods(clazz);
		List<IocInjector> injectors = new ArrayList<IocInjector>();
		IocInjector injector;
		
		for (Field field : fields) {
			Inject inject = field.getAnnotation(Inject.class);
			if (inject != null) {
				String name = inject.value();
				if (!StringUtils.isEmpty(name)) {
					injector = createFieldInjector(field, new IocRefValue(name));
				} else {
					Class<?> fieldClass = field.getType();
					injector = createFieldInjector(field, new IocClassValue(fieldClass));
				}
				injectors.add(injector);
			}
		}

		for (Method method : methods) {
			Inject inject = method.getAnnotation(Inject.class);
			if (inject != null) {
				injectors.add( IocUtils.createMethodInjector(method) );
			}
		}
		
		if(setting!=null) {
			//看下有没有要repalce的
			return merge(injectors, clazz, setting);
		}
		
		return injectors;
	}
	
	private List<IocInjector> merge(List<IocInjector> injectors,Class<?> clazz,IocSetting setting) {
		return injectors;
	}
	private SimpleIocObjectProxy createObjectProxy(Class<?> classOfT,IocSetting setting) {
		Class<?> clazz = aopFactory.enhance(classOfT);

		final IocConstructor constructor;
		final List<IocInjector> injectors;
		if(setting!=null && (setting.getArgs()!=null || setting.getFactory()!=null ) ) {
			constructor = createConstructor(setting);
		}else {
			constructor = createConstructor(clazz);
		}
		
		if(setting!=null &&  (setting.getFields()!=null || setting.getMethods()!=null)){
			injectors = createInjectors(clazz,setting);
		}else {
			injectors = createInjectors(clazz,null);
		}
		
		return new SimpleIocObjectProxy(constructor, injectors.toArray(
				new IocInjector[  injectors.size()  ]
				));
	}

	private SimpleIocObjectProxy createObjectProxy(Class<?> classOfT) {
		IocSetting setting = searchSettingByClassName(classOfT.getName());

		return createObjectProxy(classOfT, setting);
	}

	

	
}
