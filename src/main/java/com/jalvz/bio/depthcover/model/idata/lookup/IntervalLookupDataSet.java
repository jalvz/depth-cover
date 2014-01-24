package com.jalvz.bio.depthcover.model.idata.lookup;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import com.google.common.collect.Lists;
import com.jalvz.bio.depthcover.model.idata.IntervalData;
import com.jalvz.bio.depthcover.model.idata.IntervalDataBuilder;

public class IntervalLookupDataSet {
	
	private final Map<String, Set<IntervalLookupData>> intervalLookupDataMap;
	
	
	public IntervalLookupDataSet() {
		intervalLookupDataMap = new HashMap<String, Set<IntervalLookupData>>();
	}
	

	public void add(String ref, int start, int end) {
		add(ref, start, end, null);
	}
	
	
	public void add(String ref, int start, int end, String label) {
		Set<IntervalLookupData> currentSet = get(ref);
		currentSet.add(new IntervalLookupData(ref, label, start, end));
		intervalLookupDataMap.put(ref, currentSet);
	}

	
	public Set<IntervalLookupData> getReferenceIntervalData(String ref) {
		return get(ref);
	}

	
	/*
 	 * Intervals are identified by [start pos, lenght]
	 */
	public final boolean containsSL(String referenceName, int[] interval) {
		for (IntervalLookupData intervalLookupData : new RefIterator(referenceName)) {
			if (intersectsSE(new int[]{interval[0], interval[0] + interval[1] - 1}, intervalLookupData.getInterval())) {
				return true;
			}
		}
		return false;
	}

		
	/*
 	 * Intervals are identified by [start pos, end pos]
	 */
	public final List<IntervalData> intersectionsSE(String sample, String ref, int[] interval) {
		List<IntervalData> intersections = Lists.newArrayList();
		for (IntervalLookupData intervalLookupData : new RefIterator(ref)) {
			String intervalId = intervalLookupData.getId();
			if (intersectsSE(interval, intervalLookupData.getInterval())) {
				int[] intersection = intersectionSE(interval, intervalLookupData.getInterval());
				int start = intervalLookupData.getStart();
				int end = intervalLookupData.getEnd();
				intersections.add(new IntervalDataBuilder(sample, intervalId, intervalLookupData.getReference(), start, end).addIntervalSE(intersection).build());
			}
		}
		return intersections;
	}
	
	
	/*
	 * Assumes that intervals intersects.
	 * Intervals are identified by [start pos, end pos]
	 * Result interval is expressed as [closed, open)
	 */
	public final int[] intersectionSE(int[] interval, int[] lookup) {
		if (!intersectsSE(interval, lookup)) {
			throw new IllegalArgumentException("intervals [" + interval[0] + "," + interval[1] + "] , [" + lookup[0] + "," + lookup[1] + "] do not overlap");
		}
		return new int[]{Math.max(interval[0], lookup[0]), Math.min(interval[1], lookup[1]) + 1};
	}

	
	private boolean intersectsSE(int[] interval0, int[] interval1) {
		return interval0[0] <= interval1[1] && interval0[1] >= interval1[0];
	}
	

	
	private Set<IntervalLookupData> get(String ref) {
		if (intervalLookupDataMap.containsKey(ref)) {
			return intervalLookupDataMap.get(ref);
		} else {
			return new TreeSet <IntervalLookupData>(
				new Comparator<IntervalLookupData>() {
					@Override
					public int compare(IntervalLookupData o1, IntervalLookupData o2) {
						if (o1.getReference().equals(o2.getReference())) {
							return o1.getStart().compareTo(o2.getStart());
						}
						return o1.getReference().compareTo(o2.getReference());
					}
				}
			);
		}
	}

	
	
	private class RefIterator implements Iterable<IntervalLookupData> {
		
		private final Set<IntervalLookupData> refData;
		
		private RefIterator(String ref) {
			this.refData = get(ref);
		}

		@Override
		public Iterator<IntervalLookupData> iterator() {
			return refData.iterator();
		}
	}

}
