package com.jzoom.zoom.server;

import java.io.File;
import java.io.FileFilter;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ThreadFactory;

import org.apache.commons.io.monitor.FileAlterationListener;
import org.apache.commons.io.monitor.FileAlterationMonitor;
import org.apache.commons.io.monitor.FileAlterationObserver;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.beetl.core.lab.TestUser;
import org.jzoom.zoom.common.Service;

import com.jzoom.zoom.common.filter.Filter;
import com.jzoom.zoom.common.filter.pattern.PatternFilterFactory;
import com.jzoom.zoom.common.queue.ServiceThread;
import com.jzoom.zoom.common.queue.SingleEventQueue;

public class DebugServer implements Service, FileAlterationListener {

	
	private static final Log log = LogFactory.getLog(DebugServer.class);
	
	private List<String> classPath;
	private Collection<String> monitorPath;
	private final AbsServer server;
	private FileAlterationMonitor monitor;
	private ServiceThread monitorThread;
	private Filter<String> scanFilter;
	/**
	 * 监控文件变化的间隔毫秒
	 */
	private int interval = 3000;

	public DebugServer(final AbsServer server) {
		this.scanFilter = PatternFilterFactory.createFilter("!/.*&&!*.log&&!*.db&&!*.git*");
		this.classPath = ServerUtils.getClassPath();
		this.monitorPath = ServerUtils.getMonitorPath(classPath);
		this.server = server;
		this.monitorThread = new ServiceThread() {
			
			@Override
			protected boolean repetitionRun() {
				
				try {
					Thread.sleep(3000);
				} catch (InterruptedException e) {
					
					return false;
				}
				
			
				if(!server.isAlive()) {
					if(lastUpdateTime > 0 && System.currentTimeMillis() - lastUpdateTime > 3000) {
						log.error("服务挂掉了，重启中...");
						server.restart();
					}
					
					
				}
				
				
				
				return true;
			}
		};
		
	
	}
	
	
	public void setScanFilter(String filter) {
		this.scanFilter = PatternFilterFactory.createFilter(filter);
	}
	
	private Filter<String> getScanFilter(){
		
		return this.scanFilter;
	}

	private class FileObserver extends FileAlterationObserver {

		/**
		 * 
		 */
		private static final long serialVersionUID = -7184962157314935057L;

		public FileObserver(File directory) {
			super(directory, new FileFilter() {

				@Override
				public boolean accept(File file) {
					String name = file.getName();
					return getScanFilter().accept(name);
				}
			});
		}
	}

	@Override
	public void onStart(FileAlterationObserver observer) {
		// TODO Auto-generated method stub
		// System.out.println(observer);
	}

	@Override
	public void onDirectoryCreate(File directory) {
	}

	@Override
	public void onDirectoryChange(File directory) {

	}

	@Override
	public void onDirectoryDelete(File directory) {

	}

	private long lastUpdateTime = 0;
	
	private SingleEventQueue queue = new SingleEventQueue() {
		
		@Override
		protected void doJob(Object event) {
			try {
				lastUpdateTime = System.currentTimeMillis();
				restartServer();
			} catch (Exception e) {
				log.fatal("启动失败，发生错误", e);
			}
		}
	};
	
	private void onChange(File file) {
		log.info(String.format("发现修改[%s]，正在重启...",file));
		//投递线程
		queue.add(this);
	}
	
	
	/**
	 * 重启服务
	 */
	private void restartServer() {
		server.restart();
	}

	@Override
	public void onFileCreate(File file) {
		onChange(file);
	}

	@Override
	public void onFileChange(File file) {
		onChange(file);
	}

	@Override
	public void onFileDelete(File file) {
	}

	@Override
	public void onStop(FileAlterationObserver observer) {

	}

	@Override
	public void startup() throws Exception {

		startupMonitor();
		queue.startup();
		this.monitorThread.start();
		startupServer();
	

	}

	private void startupServer() throws Exception {
		server.startup();
	}

	private void startupMonitor() throws Exception {
		this.monitor = new FileAlterationMonitor(interval);
		this.monitor.setThreadFactory(new ThreadFactory() {

			@Override
			public Thread newThread(Runnable arg0) {
				Thread thread = new Thread(arg0);
				thread.setDaemon(true);
				return thread;
			}
		});
		
		for (String path : monitorPath) {
			FileObserver observer = new FileObserver(new File(path));
			this.monitor.addObserver(observer);
			observer.addListener(this);
		}
		
		this.monitor.start();

	}

	@Override
	public void shutdown() {
		shutdownMonitor();
		shutdownServer();
		this.monitorThread.stop();
		queue.shutdown();
	}

	private void shutdownServer() {
		server.shutdown();
		
	}

	private void shutdownMonitor() {
		try {
			this.monitor.stop();
		} catch (Exception e) {
			
		}
		
	}

	public int getInterval() {
		return interval;
	}

	public void setInterval(int interval) {
		this.interval = interval;
	}

}
