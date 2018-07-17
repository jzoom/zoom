package com.jzoom.zoom.server;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.AbstractHandler;

import com.jzoom.zoom.common.Service;


public abstract class AbsServer implements Service{

	private Server server;
	private int port = 8080;

	
	
	protected abstract AbstractHandler createHandler();
	



	public int getPort() {
		return port;
	}
	
	public abstract boolean isAlive();



	public void setPort(int port) {
		this.port = port;
	}

	@Override
	public void startup() throws Exception {
		if(server!=null)return;
		server = new Server(port);
		server.setHandler(createHandler());
		server.start();
		server.join();
		
	}
	
	
	public void restart() {
		try {
			server.stop();
		} catch (Exception e) {
			throw new RuntimeException("停止服务失败",e);
		}
		try {
			server.start();
		} catch (Exception e) {
			throw new RuntimeException("启动服务失败",e);
		}
	}

	@Override
	public void shutdown() {
		if(server!=null) {
			try {
				server.stop();
			} catch (Exception e) {
				
			}
			server.destroy();
			server = null;
		}
	}

}
