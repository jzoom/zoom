package com.jzoom.zoom.ioc.value;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.jzoom.zoom.common.config.ConfigReader;
import com.jzoom.zoom.ioc.impl.SimpleIocObjectProxy;
import com.jzoom.zoom.ioc.impl.SimpleObjectProxyMaker;

public class SimpleValueParser implements IocValueParser{

	private static Pattern RULE_PATTERN = Pattern.compile("^\\$\\{([a-zA-Z0-9_\\:]+)\\}$");
	
	private SimpleObjectProxyMaker maker;
	
	public SimpleValueParser(SimpleObjectProxyMaker maker) {
		this.maker = maker;
	}
	
	
	@Override
	public IocValuePair parse(Object value) {
		if(value instanceof String) {
			String str = (String)value;
			Matcher matcher = RULE_PATTERN.matcher(str);
			if(matcher.matches()) {
				String pattern = matcher.group(1);
				int indexOfColon;
				if( (indexOfColon = pattern.indexOf(':')) > 0  ) {
					String prefix = pattern.substring(0,indexOfColon);
					String endfix = pattern.substring(indexOfColon+1);
					if("cfg".equals(prefix)) {
						return createConfig(endfix);
					}else if("env".equals(prefix)) {
						return new SimpleIocValuePair(String.class, new IocEnvValue(endfix));
					}else if("ref".equals(prefix)) {
						return new SimpleIocValuePair( getRefClass(endfix), new IocRefValue(endfix));
					}else {
						throw new RuntimeException("不支持的模式"+prefix+"目前支持的模式为:\n"+ 
								"1、ref:bean名称  如 ${ref:mybean}\n" + 
								"2、cfg:配置名称  如 ${cnf:myconfig}\n" + 
								"3、env:环境变量名称 如 ${env:PATH}\n");
					}
				}else {
					return createConfig(pattern);
				}
			}
		}
		return new SimpleIocValuePair(value.getClass(), new IocRawValue(value));
	}
	
	private SimpleIocValuePair createConfig(String name) {
		Object configValue = ConfigReader.getDefault().get(name);
		return new SimpleIocValuePair(configValue.getClass(), new IocRawValue(configValue));
	}
	
	
	private Class<?> getRefClass(String endfix) {
		
		SimpleIocObjectProxy proxy = maker.getObjectProxy(endfix);
		
		return proxy.getType();
	}


	private static class SimpleIocValuePair implements IocValuePair{
		
		private Class<?> type;
		private IocValue iocValue;
		
		public SimpleIocValuePair(Class<?> type,IocValue value) {
			this.type = type;
			this.iocValue = value;
		}

		@Override
		public Class<?> getType() {
			return type;
		}

		@Override
		public IocValue getValue() {
			return iocValue;
		}
		
	}
	
	
}
