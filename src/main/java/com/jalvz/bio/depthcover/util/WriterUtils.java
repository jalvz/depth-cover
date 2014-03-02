package com.jalvz.bio.depthcover.util;

public class WriterUtils {

	
	public static double getProportion(long total, long partial) {
		return (total > 0d) ? Math.round(new Double(partial) * 1000000D / new Double(total)) / 1000000D : -1d;
	}
	
	
	public static String toStr(Number n) {
		if (n.longValue() >= 0) {
			return n.toString();
		} else {
			return "unknown";
		}
	}
	
}
