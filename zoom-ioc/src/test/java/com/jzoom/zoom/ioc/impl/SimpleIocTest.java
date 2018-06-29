package com.jzoom.zoom.ioc.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import com.jzoom.zoom.aop.annotation.Log;
import com.jzoom.zoom.aop.javassist.SimpleAopFactory;
import com.jzoom.zoom.common.config.ConfigReader;
import com.jzoom.zoom.common.json.JSONConfig;
import com.jzoom.zoom.common.logger.Loggers;
import com.jzoom.zoom.common.res.ResLoader;
import com.jzoom.zoom.ioc.IocContainer;
import com.jzoom.zoom.ioc.IocSetting;
import com.jzoom.zoom.ioc.IocSettingLoader;
import com.jzoom.zoom.ioc.annonation.Inject;

import test.beans.MyBean;
import test.beans.impl.MyBeanImpl;

public class SimpleIocTest {

	@Before
	public void setUp() throws Exception {
		
	}

	public static class B{
		
	}
	
	public static class C{
		
		public A a;
		
		public C(A a) {
			this.a = a;
		}
		
	}
	
	public static interface D{
		
	}
	
	public static class DImpl implements D{
		
	}
	
	public static class E extends DImpl {
		
	}
	
	public static class A {
		public B b;
		
		@Inject
		public C c;
		
		@Inject(value="d")
		private D d;
		
		public A(B b) {
			this.b = b;
		}

		public D getD() {
			return d;
		}

		public void setD(D d) {
			this.d = d;
		}
		
		private E e;
		
		@Inject
		public void setE(E e) {
			this.e = e;
		}
		
		boolean inited;
		
		@Inject
		public void init(E e,B b, @Inject(value="d") D d) {
			inited = true;
		}
		
		@Log
		public void test() {
			
		}
		
		@Override
		public String toString() {
			return "A";
		}
	}
	
	
	public void test() throws IOException {
		SimpleIocContainer ioc = new SimpleIocContainer(new ClassEnhanceAdapter());
		ioc.register("d", DImpl.class);
		
		//ioc.register(A.class);
		
		A a = ioc.get(A.class);
		assertNotNull(a.b);
		
		A a1= ioc.get(A.class);
		assertTrue(a==a1);
		
		
		assertTrue(a.b==ioc.get(B.class) && a.b!=null);
		
		assertTrue(a.c == ioc.get(C.class) && a.c!=null);
		
		assertTrue(ioc.get(C.class).a == a && a!=null);
		
		assertTrue(ioc.get("d") == ioc.get(A.class).d && ioc.get("d")!=null);
		
		assertTrue(ioc.get(E.class) == ioc.get(A.class).e && a.e!=null);
		
		assertTrue(a.inited);
		
		a.test();
		
		
	}
	
//	
//	@Test
//	public void testLoadFromConfig() {
//		ConfigReader reader = ConfigReader.getDefault();
//		reader.load( ResLoader.getResourceAsFile("application.properties") );
//	
//		IocSettingLoader loader = new SimpleIocSettingLoader();
//		IocSetting[] settings = loader.load();
//		
//		SimpleIocContainer ioc = new SimpleIocContainer(new SimpleIocClassFactory(new ClassEnhanceAdapter()));
//		
//		//纯接口bean
//		MyBean myBean = ioc.get("mybean");
//		assertNotNull(myBean);
//		
//		//
//		
//		MyBeanImpl myBeanImpl = ioc.get("beanwithparam");
//		assertNotNull(myBeanImpl);
//		assertEquals(myBeanImpl.param1, "param1");
//		
//		
//		assertTrue(  String.class.isAssignableFrom(String.class) );
//	}

}
