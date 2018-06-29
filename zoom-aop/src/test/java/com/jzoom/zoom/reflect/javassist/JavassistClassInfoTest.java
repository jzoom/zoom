package com.jzoom.zoom.reflect.javassist;

import static org.junit.Assert.assertEquals;

import java.lang.reflect.Method;
import java.util.List;

import org.junit.Test;

import com.jzoom.zoom.common.utils.Classes;
import com.jzoom.zoom.reflect.SimpleClassInfo;

public class JavassistClassInfoTest {
	
	public static class Test1{
		public void test(String arg0,String myArg) {
			
		}
		
		public void test1() {
			
		}
		
		
		
	}

	@Test
	public void testClassInfo(){
		SimpleClassInfo info = new SimpleClassInfo();
		List<Method> methods = Classes.getPublicMethods(Test1.class);
		for (Method method : methods) {
			
			String[] names = info.getParameterNames(Test1.class, method  );
			if(method.getName().equals("test")) {
				assertEquals(names.length, 2);
				assertEquals(names[0], "arg0");
				assertEquals(names[1], "myArg");
			}else {
				assertEquals(names.length, 0);
			}
		}
	}
	
}
