package com.jzoom.zoom.server;

import java.io.IOException;

import org.eclipse.jetty.server.handler.AbstractHandler;
import org.eclipse.jetty.webapp.WebAppClassLoader;
import org.eclipse.jetty.webapp.WebAppContext;

public class WebAppServer extends AbsServer {
	
	private String resourceBase = "src/main/webapp";
	private String contextPath = "/";
	
	private WebAppContext webAppContext;
	
	private class ZoomServerClassLoader extends WebAppClassLoader{

		public ZoomServerClassLoader(Context context) throws IOException {
			super(context);
		}
		
		@Override
		public Class<?> loadClass(String name) throws ClassNotFoundException {
			try {
				return super.loadClass(name,false);
			}catch (NoClassDefFoundError e) {
				throw new ClassNotFoundException( name );
			}
			
		}
		
		@Override
		public String toString() {
			return "ZoomServerClassLoader";
		}
		
	}
	
	@Override
	public void restart() {
		try {
			webAppContext.stop();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		configClassLoader();
		try {
			webAppContext.start();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	void configClassLoader() {
		WebAppClassLoader loader;
		try {
			String str =  System.getProperty("java.class.path");
			String[] parts = str.split( ":" ); 
			loader = new ZoomServerClassLoader(webAppContext);
			for (String part : parts) {
				if(part.endsWith("target/classes") || part.endsWith("target/test-classes")) {
					loader.addClassPath(part);
				}
				
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		webAppContext.setClassLoader( loader);
	}
	
	@Override
	protected AbstractHandler createHandler() {
		webAppContext = new WebAppContext();
		webAppContext.setResourceBase(resourceBase);
		webAppContext.setContextPath(contextPath);
		
		configClassLoader();
		return webAppContext;
	}
	
	
	
	@Override
	public void shutdown() {
		super.shutdown();
		if(webAppContext!=null) {
			try {
				webAppContext.stop();
			} catch (Exception e) {
				throw new RuntimeException(e);
			}finally {
				webAppContext.destroy();
				webAppContext = null;
			}
			
		}
	}



	public String getResourceBase() {
		return resourceBase;
	}

	public void setResourceBase(String resourceBase) {
		this.resourceBase = resourceBase;
	}

	public String getContextPath() {
		return contextPath;
	}

	public void setContextPath(String contextPath) {
		this.contextPath = contextPath;
	}

	@Override
	public boolean isAlive() {
		
		return webAppContext.isAvailable();
	}

	
	
}
