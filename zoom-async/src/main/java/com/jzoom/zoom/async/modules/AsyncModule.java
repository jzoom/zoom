package com.jzoom.zoom.async.modules;

import com.jzoom.zoom.aop.AopFactory;
import com.jzoom.zoom.async.JobQueue;
import com.jzoom.zoom.async.aop.AsyncMethodAopMaker;
import com.jzoom.zoom.async.impl.Asyncs;
import com.jzoom.zoom.ioc.IocContainer;
import com.jzoom.zoom.ioc.annonation.Inject;
import com.jzoom.zoom.ioc.annonation.Module;

@Module
public class AsyncModule {

	@Inject
	public void config( AopFactory factory,IocContainer ioc ) {
		ioc.register(JobQueue.class.getName(), Asyncs.defaultJobQueue());
		factory.addMaker(new AsyncMethodAopMaker(),0);
	}
	
}
