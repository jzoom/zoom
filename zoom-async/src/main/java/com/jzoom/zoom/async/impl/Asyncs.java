package com.jzoom.zoom.async.impl;

import com.jzoom.zoom.async.JobQueue;


/**
 * 提供对外的factory
 * @author jzoom
 *
 */
public class Asyncs {
	

	static AsyncService service = new AsyncService(20);
	
	
	
	/**
	 * 默认异步任务队列
	 * 
	 * @return
	 */
	public static JobQueue defaultJobQueue() {
		return service;
	}
	
	/**
	 * 新的异步任务队列
	 * @param threadCount
	 * @return
	 */
	public static JobQueue newJobQueue(int threadCount) {
		return new AsyncService(threadCount);
	}
}
