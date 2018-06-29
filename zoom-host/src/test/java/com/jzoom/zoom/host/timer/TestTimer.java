package com.jzoom.zoom.host.timer;

import com.jzoom.zoom.timer.annotation.Timer;

public class TestTimer {

	
	@Timer("${timer}")
	public void test() {
		System.out.print("timer");
	}
	
}
