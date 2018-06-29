package com.jzoom.zoom.web;

import java.io.File;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.util.Map;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.jzoom.zoom.aop.AopFactory;
import com.jzoom.zoom.aop.javassist.SimpleAopFactory;
import com.jzoom.zoom.caster.Caster;
import com.jzoom.zoom.caster.Caster.CasterProvider;
import com.jzoom.zoom.caster.ValueCaster;
import com.jzoom.zoom.common.config.ConfigReader;
import com.jzoom.zoom.common.filter.Filter;
import com.jzoom.zoom.common.filter.OrFilter;
import com.jzoom.zoom.common.filter.pattern.PatternFilterFactory;
import com.jzoom.zoom.common.res.ClassResolvers;
import com.jzoom.zoom.common.res.ResLoader;
import com.jzoom.zoom.common.res.ResScanner;
import com.jzoom.zoom.common.utils.CachedClasses;
import com.jzoom.zoom.common.utils.Classes;
import com.jzoom.zoom.configuration.SimpleConfigBuilder;
import com.jzoom.zoom.ioc.ClassEnhance;
import com.jzoom.zoom.ioc.IocContainer;
import com.jzoom.zoom.ioc.IocSetting;
import com.jzoom.zoom.ioc.IocSettingLoader;
import com.jzoom.zoom.ioc.impl.SimpleIocClassFactory;
import com.jzoom.zoom.ioc.impl.SimpleIocContainer;
import com.jzoom.zoom.ioc.impl.SimpleIocSettingLoader;
import com.jzoom.zoom.reflect.ClassInfo;
import com.jzoom.zoom.reflect.SimpleClassInfo;
import com.jzoom.zoom.web.action.ActionHandler;
import com.jzoom.zoom.web.action.impl.SimpleActionBuilder;
import com.jzoom.zoom.web.classloader.WebClassLoader;
import com.jzoom.zoom.web.constants.ConfigurationConstants;
import com.jzoom.zoom.web.exception.StatusException;
import com.jzoom.zoom.web.router.RouterParamRule;
import com.jzoom.zoom.web.router.impl.BracesRouterParamRule;
import com.jzoom.zoom.web.router.impl.SimpleRouter;
import com.jzoom.zoom.web.utils.WebUtils;

public class ZoomWeb {
	private SimpleRouter router;

	private IocContainer ioc;
	private AopFactory factory;
	
	private WebClassLoader classLoader;

	private static Log log = LogFactory.getLog(ZoomWeb.class);
	long time = System.currentTimeMillis();
	private Long[] times = new Long[10];
	private int timeIndex;
	private String[] names = new String[10];

	private void step(String name) {
		long now = System.currentTimeMillis();
		times[timeIndex] = now - time;
		names[timeIndex] = name;
		time = now;
		++timeIndex;
	}

	@SuppressWarnings("unchecked")
	public void init() {
		long first = System.currentTimeMillis();
		log.info("==============================Startup Zoom==============================");

		System.out.println("____  ___    ___\n" + "  /  / _ \\  / _ \\ |\\    /|\n"
				+ " /  | (_) || (_) || \\  / |    \n" + "/___ \\___/  \\___/ |  \\/  |\n");
		/// 加载整个项目的主配置
		loadApplicationConfig();
		step("config");
		// 初始化ioc容器
		createIocContainer();
		step("ioc");
		// 扫描整个资源, .class .jar 其他配置文件等
		scanResources();
		step("res");
		// 初始化router
		ioc.register(RouterParamRule.class, BracesRouterParamRule.class);
		router = ioc.get(SimpleRouter.class);
		// 初始化classInfo
		ioc.register(ClassInfo.class, SimpleClassInfo.class);

		ClassResolvers visitors = new ClassResolvers(
				ioc.get(SimpleConfigBuilder.class),
				ioc.get(SimpleActionBuilder.class)
				);
		visitors.visit();
		
		step("controllers");

		RuntimeMXBean bean = ManagementFactory.getRuntimeMXBean();
		long startTime = bean.getStartTime();
		long now = System.currentTimeMillis();
		if (log.isDebugEnabled()) {
			for (int i = 0; i < timeIndex; ++i) {
				log.debug(names[i] + ":" + times[i]);
			}
		}
		log.info(String.format(
				"==============================Startup Zoom  in [%d] ms , JVM runing time [%d] ms==============================",
				now - first, now - startTime));

	}
	
	private void createAopFactory() {
		classLoader = new WebClassLoader(ZoomWeb.class.getClassLoader());
		factory = new SimpleAopFactory(classLoader);
	}
	
	private void createIocContainer() {
		createAopFactory();
		
		IocSettingLoader loader = new SimpleIocSettingLoader();
		IocSetting[] settings = loader.load();
		ioc = new SimpleIocContainer(new SimpleIocClassFactory(new ClassEnhanceAdapter(factory)));
	//	ioc = new GlobalIocContainer(new ClassEnhanceAdapter(factory),settings);
		ioc.register(AopFactory.class.getName(), factory);
		WebUtils.setIoc(ioc);
		
	}
	
	

	public class ClassEnhanceAdapter implements ClassEnhance {
		private AopFactory factory;
		public ClassEnhanceAdapter( AopFactory factory) {
			this.factory = factory;
		}
		

		@Override
		public Class<?> enhance(Class<?> src) {
			return factory.enhance(src);
		}

	}

	class JarFilter implements Filter<File> {

		private Filter<String> filter;

		public JarFilter(Filter<String> filter) {
			this.filter = filter;
		}

		@Override
		public boolean accept(File value) {
			return filter.accept(value.getName());
		}

	}

	@SuppressWarnings("unchecked")
	private void scanResources() {
		Filter<File> jarFilter = null;
		String jar = ConfigReader.getDefault().getString(ConfigurationConstants.SCAN_JAR);
		if (!StringUtils.isEmpty(jar)) {
			jarFilter = new OrFilter<File>(ResScanner.fastFilter,
					new JarFilter(PatternFilterFactory.createFilter(jar)));
		} else {
			// 不扫描jar
			jarFilter = ResScanner.fastFilter;
		}

		try {
			ResScanner.me().scan(ZoomFilter.class.getClassLoader(), jarFilter);
		} catch (IOException e) {
			throw new RuntimeException("扫描解析文件出错，请确认权限是否满足要求");
		}

	}

	/**
	 * 获取应用程序全局配置
	 * 
	 * @return
	 */
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

	public void destroy() {
		
		if(ioc!=null) {
			ioc.destroy();
			ioc = null;
		}
		
		
		router = null;
		factory = null;
		classLoader = null;
		
		WebUtils.setIoc(null);
		
		CachedClasses.clear();
		PatternFilterFactory.clear();
		ResScanner.me().destroy();
	}

	public boolean handle(ServletRequest req, ServletResponse resp) throws Exception {
		// 路由
		HttpServletRequest request = (HttpServletRequest) req;

		ActionHandler action = router.match(request);
		if (action == null) {
			return false;
		}

		HttpServletResponse response = (HttpServletResponse) resp;
		if(action.handle(request, response)) {
			return true;
		}
		throw new StatusException.NotAllowedHttpMethodException();
	}
}
