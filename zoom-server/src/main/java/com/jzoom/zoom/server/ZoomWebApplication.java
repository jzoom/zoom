package com.jzoom.zoom.server;

public class ZoomWebApplication {

	public static void start(Class<?> clazz) throws Exception {
		
		WebAppServer app = new WebAppServer();
		app.setPort(8090);
		app.setContextPath("/");
		app.setResourceBase("src/main/webapp");
		DebugServer server = new DebugServer(app) ;
		server.setScanFilter("!.*&!*.log&!*.db&!*.git*&!*.html&!*.js&!*.css");
		server.setInterval(1000);
		server.startup();
	}
}
