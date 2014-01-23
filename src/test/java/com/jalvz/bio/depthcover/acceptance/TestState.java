package com.jalvz.bio.depthcover.acceptance;

public class TestState {

	private static TestState instance = new TestState();

	protected String r1, r2;
	
	private TestState() {}

	public static TestState getInstance() {
		return instance;
	} 
}
