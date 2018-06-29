package com.jzoom.zoom.ioc.impl;

import java.util.List;
import java.util.Map;

import com.jzoom.zoom.common.config.ConfigReader;
import com.jzoom.zoom.ioc.IocSetting;
import com.jzoom.zoom.ioc.IocSettingLoader;

/**
 * 
 * {@link com.jzoom.zoom.common.config.PropertiesConfigReader}\n
 * properties里面存储bean\n
 * 形式为:
 * 
 * bean[0].name=bean名称
 * bean[0].class=类
 * bean[0].factory=类名称#方法名称/bean名称#方法名称
 * bean[0].args[]=参数0
 * bean[0].args[]=参数1
 * bean[0].field{}.fieldName[0]=field注入值0
 * bean[0].field{}.fieldName[1]=field注入值1
 * bean[0].method{}.methodName[0]=参数0
 * 
 * 
 * class与factory任意写一个，但是不能都为空
 * 
 * 构造参数列表:   参数1,参数2,参数3
 * 
 * 参数形式看这里:{@link com.jzoom.zoom.ioc.value.IocValue}
 * 
 * 如果参数是字符串，并且里面有,  则整个字符串用""括起来
 * 如:  bean.mybean.args="1,2,3",hash   表示两个参数: 参数1：1,2,3 参数2:hash
 * 支持这种形式:
 * bean.mybean.args={}    表示一个map或一个bean   
 * 如
 * class MyClass{
 * 	public MyClass(A a){}
 * }
 * 
 * class A{
 * 	public A(int a,int b){}
 * }
 * 
 * 那么可以这么写:
 * bean.myclass.args={a:1,b:1}     框架在构造myclass的时候会内联创建一个A，使用参数a=1,b=1
 * 也可以这么写
 * 
 * bean[0].name=a
 * bean[0].class=A
 * bean[0].args=1,1
 * 
 * bean[1].name=myclass
 * bean[1].class=MyClass
 * bean[1].args=${ref:a}
 * 
 * 
 * @author jzoom
 *
 */
public class SimpleIocSettingLoader implements IocSettingLoader {
	
	//bean[0].field.fieldName=参数
	@SuppressWarnings("unchecked")
	@Override
	public IocSetting[] load() {
		
		List<Map<String, Object>> beans = (List<Map<String, Object>>) ConfigReader.getDefault().get("bean");
		if(beans==null) {
			return new IocSetting[] {};
		}
		
		int index = 0;
		IocSetting[] settings = new IocSetting[beans.size()];
		for (Map<String, Object> map : beans) {
			settings[index++] = convet(map);
		}
		
		return settings;
	}

	private IocSetting convet(Map<String, Object> map) {
		IocSetting setting = new SimpleIocSetting(map);
		return setting;
	}

	
	
}
