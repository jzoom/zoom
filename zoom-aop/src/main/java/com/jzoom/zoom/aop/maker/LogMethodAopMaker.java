package com.jzoom.zoom.aop.maker;

import java.lang.reflect.Method;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.jzoom.zoom.aop.MethodInterceptor;
import com.jzoom.zoom.aop.MethodInvoker;
import com.jzoom.zoom.aop.annotation.Log;
import com.jzoom.zoom.common.logger.Logger;
import com.jzoom.zoom.common.logger.Loggers;
import com.jzoom.zoom.common.utils.Classes;

public class LogMethodAopMaker extends AnnotationMethodAopMaker<Log> {

	public static class MethodLogInterceptor implements MethodInterceptor{

		private Logger Logger = Loggers.getLogger();
		
		@Override
		public void intercept(MethodInvoker invoker) throws Throwable {
			try {
				invoker.invoke();
				Logger.info("目标对象[%s] 方法[%s] 参数[%s] 执行结果[%s]", invoker.getTarget(), invoker.getMethod(),StringUtils.join(invoker.getArgs(),",") ,invoker.getReturnObject());
			}catch (Throwable e) {
				Logger.error(e,"目标对象[%s] 方法[%s] 参数[%s] 执行发生异常[%s]",invoker.getTarget(), invoker.getMethod(),StringUtils.join(invoker.getArgs(),",") , Classes.getCause(e) );
				throw Classes.getCause(e);
			}
			
		}
		
	}
	
	MethodLogInterceptor defaultInterceptor = new MethodLogInterceptor();
	
	
	@Override
	protected void makeAops(Log annotation, Method method, List<MethodInterceptor> interceptors) {
		interceptors.add(  defaultInterceptor );
	}

}
