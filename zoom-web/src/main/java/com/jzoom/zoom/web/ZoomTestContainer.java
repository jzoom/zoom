package com.jzoom.zoom.web;

import java.io.File;

import com.jzoom.zoom.aop.AopFactory;
import com.jzoom.zoom.aop.javassist.SimpleAopFactory;
import com.jzoom.zoom.common.config.ConfigReader;
import com.jzoom.zoom.common.res.ResLoader;
import com.jzoom.zoom.ioc.ClassEnhance;
import com.jzoom.zoom.ioc.IocContainer;
import com.jzoom.zoom.ioc.IocSetting;
import com.jzoom.zoom.ioc.IocSettingLoader;
import com.jzoom.zoom.ioc.impl.SimpleIocClassFactory;
import com.jzoom.zoom.ioc.impl.SimpleIocContainer;
import com.jzoom.zoom.ioc.impl.SimpleIocSettingLoader;
import com.jzoom.zoom.web.ZoomWeb.ClassEnhanceAdapter;
import com.jzoom.zoom.web.classloader.WebClassLoader;
import com.jzoom.zoom.web.utils.WebUtils;

public class ZoomTestContainer {
	protected IocContainer ioc;
	private AopFactory factory;

	private WebClassLoader classLoader;

	public ZoomTestContainer() {
		loadApplicationConfig();
		createIocContainer();
	}
	private void loadApplicationConfig() {
		// 加载全局配置

		File file = ResLoader.getResourceAsFile("application.properties");
		if (file == null) {
			file = ResLoader.getResourceAsFile("application.json");
		}

		if (file == null) {
			// 目前这个版本支持两种主配置，properties/yml
			throw new RuntimeException("启动失败，请确认application.properties或application.json存在");
		}
		ConfigReader.getDefault().load(file);

	}
	private void createAopFactory() {
		classLoader = new WebClassLoader(ZoomWeb.class.getClassLoader());
		factory = new SimpleAopFactory(classLoader);
	}

	public class ClassEnhanceAdapter implements ClassEnhance {
		private AopFactory factory;

		public ClassEnhanceAdapter(AopFactory factory) {
			this.factory = factory;
		}

		@Override
		public Class<?> enhance(Class<?> src) {
			return factory.enhance(src);
		}

	}

	private void createIocContainer() {
		createAopFactory();

		IocSettingLoader loader = new SimpleIocSettingLoader();
		IocSetting[] settings = loader.load();
		ioc = new SimpleIocContainer(new SimpleIocClassFactory(new ClassEnhanceAdapter(factory)));
		// ioc = new GlobalIocContainer(new ClassEnhanceAdapter(factory),settings);
		ioc.register(AopFactory.class.getName(), factory);
		WebUtils.setIoc(ioc);

	}
}
