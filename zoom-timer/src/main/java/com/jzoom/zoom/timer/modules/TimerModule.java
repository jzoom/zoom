package com.jzoom.zoom.timer.modules;

import java.lang.reflect.Method;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.jzoom.zoom.common.Destroyable;
import com.jzoom.zoom.common.el.ElParser;
import com.jzoom.zoom.common.utils.Classes;
import com.jzoom.zoom.ioc.IocContainer;
import com.jzoom.zoom.ioc.IocContainer.IocContainerListener;
import com.jzoom.zoom.ioc.IocInjector;
import com.jzoom.zoom.ioc.annonation.Inject;
import com.jzoom.zoom.ioc.annonation.IocBean;
import com.jzoom.zoom.ioc.annonation.Module;
import com.jzoom.zoom.ioc.impl.IocUtils;
import com.jzoom.zoom.timer.TimerJob;
import com.jzoom.zoom.timer.TimerService;
import com.jzoom.zoom.timer.annotation.Timer;
import com.jzoom.zoom.timer.impl.QuartzTimerService;

@Module
public class TimerModule implements IocContainerListener,Destroyable {
	
	@Inject
	private IocContainer ioc;

	@IocBean
	public TimerService getTimerService() {
		return new QuartzTimerService();
	}
	
	@Inject
	public void config(IocContainer ioc) {
		ioc.addListener(this);
	}

	class TimerData{
		IocInjector injector;
		Object target;
		IocContainer ioc;
		

		public TimerData(IocContainer ioc, Object target, IocInjector injector) {
			this.ioc = ioc;
			this.injector = injector;
			this.target = target;
		}
	}
	
	public static class IocTimerJob implements TimerJob<TimerData >{
		
		
		
		public IocTimerJob(  ) {
			
		}

		@Override
		public void execute(TimerData data) {
			data.injector.inject(data.target,data.ioc);
		}
	}
	
	@Override
	public void onCreated(Object target) {
		//logger.info(target);
		if(target instanceof TimerService) {
			return;
		}
		
		TimerService timerService = ioc.get(TimerService.class);
		
		Class<?> targetClass = target.getClass();
		List<Method> list = Classes.getPublicMethods(target.getClass());
		
		for (Method method : list) {
			if(method.isAnnotationPresent(Timer.class)) {
				Timer timer = method.getAnnotation(Timer.class);
				StringBuilder sb = new StringBuilder(targetClass.getName())
						.append("#")
						.append(method.getName());
				String cron = ElParser.parseValue(timer.value());
				if(StringUtils.isEmpty(cron))continue;
				timerService.startTimer(sb.toString(), IocTimerJob.class, new TimerData(
						ioc,
						target,
						IocUtils.createMethodInjector(method)
						), cron  );
				
			}
		}
	}

	@Override
	public void onDestroyed(Object tartet) {
		
		
	}

	@Override
	public void destroy() {
		
	}


	
}
