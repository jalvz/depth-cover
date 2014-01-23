package com.jalvz.bio.depthcover.model.cdata;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;



public class CoverageDataResultHelper {

	
	protected static Map<Integer, Long> newMap() {
		return new TreeMap<Integer, Long>(new Comparator<Integer>() {
			@Override
			public int compare(Integer o1, Integer o2) {
				return o2.compareTo(o1); 
			}
		});
	}


	protected static Map<Integer, Long> merge(List<Map<Integer, Long>> individualCoverageResults) {
		Map<Integer, Long> mutableMap = newMap();
		for (Map<Integer, Long> covRs : individualCoverageResults) {
			append(mutableMap, covRs);
		}
		return accumulateOnPreviousValues(mutableMap);
	}

	
	protected static Map<Integer, Long> accumulateOnPreviousValues(Map<Integer, Long> map) {
		long accumulatedValue = 0;
		Map<Integer, Long> target = new TreeMap<Integer, Long>();
		for (int current : map.keySet()) {
			accumulatedValue += map.get(current);
			target.put(current, accumulatedValue);
		}
		return target;
	}

	
	private static void append(Map<Integer, Long> mutableMap, Map<Integer, Long> toMerge) {
		for (int key : toMerge.keySet()) {
			if (mutableMap.containsKey(key)) {
				mutableMap.put(key, mutableMap.get(key) + toMerge.get(key));
			} else {
				mutableMap.put(key, toMerge.get(key));
			}
		}
	}

}

