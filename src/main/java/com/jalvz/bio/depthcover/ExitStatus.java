package com.jalvz.bio.depthcover;


public class ExitStatus {
	
	private static ExitStatus instance;
	
	
	private ExitStatus(int status) {
		this.status = status;
	}

	public int status;
	
	
	public synchronized static int getStatus() {
		if (instance == null) {
			instance = new ExitStatus(0);
		}
		return instance.status;
	}

	
	public static void setStatus(int status) {
		getStatus();
		instance.status = status;
	}

}
