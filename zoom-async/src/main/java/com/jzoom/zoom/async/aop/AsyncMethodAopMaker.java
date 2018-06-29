package com.jzoom.zoom.async.aop;

import java.lang.reflect.Method;
import java.util.List;
import java.util.concurrent.Callable;

import com.jzoom.zoom.aop.MethodInterceptor;
import com.jzoom.zoom.aop.MethodInvoker;
import com.jzoom.zoom.aop.maker.AnnotationMethodAopMaker;
import com.jzoom.zoom.async.annotation.Async;
import com.jzoom.zoom.async.impl.Asyncs;

public class AsyncMethodAopMaker extends AnnotationMethodAopMaker<Async>{

	@Override
	protected void makeAops(Async annotation, Method method, List<MethodInterceptor> interceptors) {
		interceptors.add(interceptor);
	}
	
	AsyncMethodInterceptor interceptor = new AsyncMethodInterceptor();
	
	private static class AsyncMethodInterceptor implements MethodInterceptor{

		@Override
		public void intercept(final MethodInvoker invoker) throws Throwable {
			
			Asyncs.defaultJobQueue().submit(new Callable<Object>() {

				@Override
				public Object call() throws Exception {
					try {
						invoker.invoke();
					} catch (Throwable e) {
						throw new RuntimeException("执行错误",e);
					}
					return null;
				}
			});
			
		}
		
	}

	

}
