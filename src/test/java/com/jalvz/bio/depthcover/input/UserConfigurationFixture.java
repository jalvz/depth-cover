package com.jalvz.bio.depthcover.input;

import com.jalvz.bio.depthcover.input.UserConfiguration;

public class UserConfigurationFixture {

	public static UserConfiguration getLowFlagNoDetails() {
		return new UserConfiguration(true, false, Integer.MAX_VALUE, "", null, null);
	}
	
	public static UserConfiguration getNoDetails() {
		return new UserConfiguration(false, false, Integer.MAX_VALUE, "sorted", null, null);
	}
	
}
