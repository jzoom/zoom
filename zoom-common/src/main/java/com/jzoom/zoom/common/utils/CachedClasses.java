package com.jzoom.zoom.common.utils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import com.jzoom.zoom.common.filter.MethodFilter;

public class CachedClasses {

	private static class ClassHolder{
		Field[] fields;
		Method[] publicMethods;
		public void clear() {
			fields = null;
			publicMethods = null;
		}
	}
	
	private static Map<Class<?>, ClassHolder> map = new ConcurrentHashMap<Class<?>, CachedClasses.ClassHolder>();
	public static void clear() {
		for (Entry<?,ClassHolder> entry : map.entrySet()) {
			entry.getValue().clear();
		}
		map.clear();
	}
	public static Field[] getFields(Class<?> clazz) {
		ClassHolder holder = map.get(clazz);
		if(holder == null) {
			synchronized (map) {
				holder = new ClassHolder();
				map.put(clazz, holder);
			}
			
			if(holder.fields==null) {
				synchronized (holder) {
					
					List<Field> fields = Classes.getFields(clazz);
					holder.fields = fields.toArray( new Field[ fields.size()]);
					
				}
			}
		}
		return holder.fields;
	}

	public static Method[] getPublicMethods(Class<?> clazz) {
		ClassHolder holder = map.get(clazz);
		if(holder == null) {
			synchronized (map) {
				holder = new ClassHolder();
				map.put(clazz, holder);
			}
			
			if(holder.publicMethods==null) {
				synchronized (holder) {
					
					List<Method> methods = Classes.getPublicMethods(clazz);
					holder.publicMethods = methods.toArray( new Method[ methods.size()]);
					
				}
			}
		}
		return holder.publicMethods;
	}
	
	
	public static List<Method> getPublicMethods(Class<?> clazz,String name) {
		List<Method> list = new ArrayList<Method>();
		Method[] methods = getPublicMethods(clazz);
		for (Method method : methods) {
			if(method.getName().equals(name)) {
				list.add(method);
			}
		}
		return list;
	}
	
	public static List<Method> getPublicMethods(Class<?> clazz,MethodFilter filter) {
		List<Method> list = new ArrayList<Method>();
		Method[] methods = getPublicMethods(clazz);
		for (Method method : methods) {
			if(filter.accept(method)) {
				list.add(method);
			}
		}
		return list;
	}
	
	public static Method fetchPublicMethod(Class<?> clazz,MethodFilter filter) {
		Method[] methods = getPublicMethods(clazz);
		for (Method method : methods) {
			if(filter.accept(method)) {
				return method;
			}
		}
		return null;
	}

	
}
