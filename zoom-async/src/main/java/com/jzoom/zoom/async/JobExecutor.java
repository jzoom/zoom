package com.jzoom.zoom.async;

public interface JobExecutor<T,R> {
	R execute(T data);
}
