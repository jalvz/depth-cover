package com.jalvz.bio.depthcover.model.idata;

public class IntervalDataFixture {

	public static IntervalDataBuilder simple(String ref) {
		return new IntervalDataBuilder("sample1", ref, 0, 25).addIntervalSL(new int[]{0,1});
	}
	
	
	public static IntervalData custom(int... evt) {
		IntervalDataBuilder builder = new IntervalDataBuilder("sample1", "chr1", 0, 25);
		for (int idx = 0; idx < evt.length; idx=idx+2) {
			builder.addIntervalSL(new int[]{evt[idx], evt[idx+1]});
		}
		return builder.build();
	}
}
