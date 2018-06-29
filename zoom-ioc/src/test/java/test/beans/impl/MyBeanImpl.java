package test.beans.impl;

import test.beans.MyBean;
import test.beans.MyBeanParam;

public class MyBeanImpl implements MyBean {

	public String param0;
	public String param1;
	
	public MyBeanParam param;
	
	public MyBeanImpl() {
		
	}
	
	public MyBeanImpl(String param0,String param1) {
		this.param0 = param0;
		this.param1 = param1;
	}
	
	public MyBeanImpl( MyBeanParam param ,String param1) {
		this.param = param;
		this.param1 = param1;
	}
	
}
