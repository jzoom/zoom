package com.jzoom.zoom.modules;


import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jzoom.zoom.common.Service;

import com.jzoom.zoom.dao.Dao;
import com.jzoom.zoom.dao.driver.mysql.MysqlConnDescription;
import com.jzoom.zoom.dao.impl.ZoomDao;
import com.jzoom.zoom.dao.provider.DruidDataSourceProvider;
import com.jzoom.zoom.host.timer.TestTimer;
import com.jzoom.zoom.ioc.IocContainer;
import com.jzoom.zoom.ioc.annonation.Inject;
import com.jzoom.zoom.ioc.annonation.IocBean;
import com.jzoom.zoom.ioc.annonation.Module;
import com.jzoom.zoom.server.DebugServer;
import com.jzoom.zoom.server.WebAppServer;
import com.jzoom.zoom.web.action.ActionContext;
import com.jzoom.zoom.web.action.ActionInterceptor;
import com.jzoom.zoom.web.action.ActionInterceptorFactory;

import junit.framework.TestCase;

@Module
public class Application extends TestCase {
	
	private static final Log log = LogFactory.getLog(Application.class);
	
	public Application() {
		
	}

	public void testApp() throws Exception {
		//JFinal.start("src/main/webapp", 8091, "/api", 3);
		WebAppServer app = new WebAppServer();
		app.setPort(8090);
		Service server = new DebugServer(app) ;
		server.startup();
	}
	
	@IocBean
	public Dao getDao() {
		DruidDataSourceProvider provider = new DruidDataSourceProvider(
				new MysqlConnDescription("127.0.0.1", 13306, "zoom", "root", "root")
				);
		DataSource dataSource = provider.getDataSource();
		Dao dao =  new ZoomDao(dataSource, true);
		return dao;
	}
	
	@Inject
	public void config( ActionInterceptorFactory factory ) {
		factory.add(new ActionInterceptor() {
			
			private long time;
			@Override
			public boolean preParse(ActionContext context) throws Exception {
				System.out.println("preParse");
				time = System.currentTimeMillis();
				//检查签名
				return true;
			}
			
			@Override
			public void parse(ActionContext context) throws Exception {
				System.out.println("parse");
				
			}
			
			@Override
			public void beforeRender(ActionContext context) throws Exception {
				System.out.println("beforeRender");
				if(context.getException()!=null) {
					
				}else {
					Map<String, Object> data = new HashMap<String, Object>();
					data.put("flag", 0);
					data.put("result", context.getResult());
					context.setResult(data);
				}
			}
			
			@Override
			public void afterRender(ActionContext context) throws Exception {
				System.out.println("afterRender");
				
			}


			@Override
			public void complete() throws Exception {
				System.out.println("complete");
				log.info("时间"+(System.currentTimeMillis() - time));
			}

			@Override
			public boolean whenError(ActionContext context) throws Exception {
				System.out.println("whenError");
				context.getException().printStackTrace();
				return true;
			}
		}, "*",  0 );
	}
	
	
	@IocBean
	public TestTimer getTimer() {
	
		return new TestTimer();
	}
}
 