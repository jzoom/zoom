package com.jzoom.zoom.admin.modules;


import java.util.Map;

import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.alibaba.druid.pool.DruidDataSource;
import com.jzoom.zoom.aop.AopFactory;
import com.jzoom.zoom.dao.Dao;
import com.jzoom.zoom.dao.driver.mysql.MysqlConnDescription;
import com.jzoom.zoom.dao.impl.ZoomDao;
import com.jzoom.zoom.dao.provider.DruidDataSourceProvider;
import com.jzoom.zoom.ioc.annonation.Inject;
import com.jzoom.zoom.ioc.annonation.IocBean;
import com.jzoom.zoom.ioc.annonation.Module;
import com.jzoom.zoom.server.ZoomWebApplication;
import com.jzoom.zoom.web.action.ActionContext;
import com.jzoom.zoom.web.action.ActionInterceptorAdapter;
import com.jzoom.zoom.web.action.ActionInterceptorFactory;

@Module
public class Application{
	
	private static final Log log = LogFactory.getLog(Application.class);
	
	@Inject("cfg:mysql")
	private MysqlConnDescription mysql;
	
	
	public Application() {
		
	}
	


	public static void main(String[] args) throws Exception {
		ZoomWebApplication.start(Application.class);
	}

	@IocBean(destroy="close")
	public DataSource getDataSource() {
		DruidDataSourceProvider provider = new DruidDataSourceProvider(mysql);
		DruidDataSource dataSource = provider.getDataSource();
		return dataSource;
	}
	
	@IocBean
	public Dao getDao(DataSource dataSource) {
		return new ZoomDao(dataSource, true);
	}
	
	@Inject
	public void config( AopFactory aopFactory ) {
		/**
		 * 生产环境最好不要这么干，*表示所有模型的所有方法都做切面，极大降低系统性能。
		 */
//		aopFactory.addFilter(new MethodInterceptor() {
//			@Override
//			public void intercept(MethodInvoker invoker) throws Throwable {
//				try {
//					invoker.invoke();
//				}finally {
//					//System.out.println(invoker.getMethod().getName());
//				}
//				
//			}
//		}, "*", 0);
	}
	

	@Inject
	public void config( ActionInterceptorFactory factory ) {
		factory.add(new ActionInterceptorAdapter() {
			
			@Override
			public boolean preParse(ActionContext context) throws Exception {
				log.info(context.getRequest().getRequestURI());
				return true;
			}
			
			@Override
			public void parse(ActionContext context) throws Exception {
				if(context.getPreParam() instanceof Map) {
					log.info( context.getPreParam());
				}
			}
			
			@Override
			public boolean whenError(ActionContext context) throws Exception {
				log.error( "出现错误", context.getException());
				return true;
			}
			
		}, "*", 0);
	}

	
}
 