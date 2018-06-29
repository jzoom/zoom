package com.jzoom.zoom.async;

public interface JobHandler<T> {
	void execute(T data);
}
