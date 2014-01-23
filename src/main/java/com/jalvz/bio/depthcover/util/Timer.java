package com.jalvz.bio.depthcover.util;

import org.apache.log4j.Logger;


public class Timer {

	private static final Logger logger = Logger.getLogger(Timer.class.getName());
	
	private final long t1;
	
	
	public Timer() {
		t1 = System.currentTimeMillis();
	}


	public static void sleep(long ms) {
		try {
			Thread.sleep(ms);
		} catch (InterruptedException e) {
			logger.debug(e.getMessage(), e);
		}
	}
	
	
	public long elapsedTime() {
		return System.currentTimeMillis() - t1;
	}
}
