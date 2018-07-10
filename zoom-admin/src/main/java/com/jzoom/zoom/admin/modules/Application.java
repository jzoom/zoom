package com.jzoom.zoom.admin.modules;


import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.alibaba.druid.pool.DruidDataSource;
import com.jzoom.zoom.aop.AopFactory;
import com.jzoom.zoom.dao.Dao;
import com.jzoom.zoom.dao.driver.mysql.MysqlConnDescription;
import com.jzoom.zoom.dao.driver.oracle.OracleConnDescription;
import com.jzoom.zoom.dao.impl.ZoomDao;
import com.jzoom.zoom.dao.provider.DruidDataSourceProvider;
import com.jzoom.zoom.ioc.IocContainer;
import com.jzoom.zoom.ioc.annonation.Inject;
import com.jzoom.zoom.ioc.annonation.IocBean;
import com.jzoom.zoom.ioc.annonation.Module;
import com.jzoom.zoom.server.ZoomWebApplication;
import com.jzoom.zoom.web.action.ActionContext;
import com.jzoom.zoom.web.action.ActionInterceptorAdapter;
import com.jzoom.zoom.web.action.ActionInterceptorFactory;
import com.sun.tools.javac.util.Name;

@Module
public class Application{
	
	private static final Log log = LogFactory.getLog(Application.class);
	
	@Inject("cfg:mysql")
	private MysqlConnDescription mysql;
	
	@Inject("cfg:oracle")
	private OracleConnDescription oracle;
	
	@Inject("cfg:aliyun")
	private MysqlConnDescription aliyun;
	
	public Application() {
		
	}
	


	public static void main(String[] args) throws Exception {
		ZoomWebApplication.start(Application.class);
	}

	@IocBean(destroy="close",name="defaultDataSource")
	public DataSource getDataSource() {
		DruidDataSourceProvider provider = new DruidDataSourceProvider(mysql);
		DruidDataSource dataSource = provider.getDataSource();
		return dataSource;
	}
//	
//	
//	@IocBean(destroy="close",name="aliyunDataSource")
//	public DataSource getAliyunDataSource() {
//		DruidDataSourceProvider provider = new DruidDataSourceProvider(aliyun);
//		DruidDataSource dataSource = provider.getDataSource();
//		return dataSource;
//	}
	
	@IocBean
	public Dao getDao( @Inject("defaultDataSource")  DataSource dataSource ) {
		return new ZoomDao(dataSource,false,null);
	}
//	
	
	
	@IocBean(name="admin")
	public Dao getDao() {
		return new ZoomDao(new RawDataSource("jdbc:h2:file:./admin","sa","sa"),false);
	}
	

//
//	@IocBean(name="aliyun")
//	public Dao getAliyunDao(
//			@Inject("aliyunDataSource")
//			DataSource dataSource) {
//		return new ZoomDao(dataSource, true);
//	}
	
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
	public void config( ActionInterceptorFactory factory ,IocContainer ioc) {
		factory.add(ioc.get(AdminActionInterceptor.class), "!*LoginController*&!*UploadController*#*", 1);
		factory.add(new ActionInterceptorAdapter() {
			 
			
			@Override
			public boolean preParse(ActionContext context) throws Exception {
				log.info("正在访问"+context.getRequest().getRequestURI() );
				return true;
			}
			
			@Override
			public void parse(ActionContext context) throws Exception {
				if(context.getPreParam() instanceof Map) {
					log.info("正在访问"+context.getRequest().getRequestURI() + " PARAM:" + context.getPreParam());
					
				}
			}
			
			@Override
			public void whenResult(ActionContext context) throws Exception {
				if(context.getPreParam() instanceof Map) {
					log.info("返回:"+context.getRequest().getRequestURI() + " RESULT:" + context.getRenderObject());
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
 