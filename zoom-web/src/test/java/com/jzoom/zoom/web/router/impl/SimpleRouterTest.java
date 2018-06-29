package com.jzoom.zoom.web.router.impl;

import static org.junit.Assert.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.Before;
import org.junit.Test;

import com.jzoom.router.test.mock.MockHttpServletRequest;
import com.jzoom.zoom.web.action.ActionHandler;

public class SimpleRouterTest {
	

	public static class TestActionHandler implements ActionHandler{
		private String name;
		public TestActionHandler(String name) {
			this.name = name;
		}

		@Override
		public boolean handle(HttpServletRequest request, HttpServletResponse response) {
			// TODO Auto-generated method stub
			return false;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}
		
		@Override
		public boolean equals(Object obj) {
			if(obj instanceof TestActionHandler) {
				return ((TestActionHandler)obj).getName().equals(getName());
			}
			return super.equals(obj);
		}
		
	}

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void test() {
		
		SimpleRouter router = new SimpleRouter(new BracesRouterParamRule());
		
		router.register("/{action}/index", new TestActionHandler("index"));
		router.register("/{action}/edit", new TestActionHandler("edit"));
		router.register("/{action}/add", new TestActionHandler("add"));
		
		assertEquals( router.match(new MockHttpServletRequest("/main/index")) ,    new TestActionHandler("index")  );
		
	}

}
