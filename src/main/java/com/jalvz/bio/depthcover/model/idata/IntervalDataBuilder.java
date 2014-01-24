package com.jalvz.bio.depthcover.model.idata;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.log4j.Logger;

import com.jalvz.bio.depthcover.util.Timer;



public class IntervalDataBuilder {

	private static final Logger logger = Logger.getLogger(IntervalDataBuilder.class.getName());

	
	private final IntervalData data;


	public IntervalDataBuilder(String sampleName, String referenceName, int start, int end) {
		this(sampleName, referenceName, referenceName, start, end);
	}

	public IntervalDataBuilder(String sample, String referenceId, String referenceName, int start, int end) {
		data = new IntervalData(sample, referenceId, referenceName, start, end);
	}


	public IntervalDataBuilder(IntervalData data) {
		this(data.getSampleName(), data.getReferenceName(), data.getOriginalReferenceName(), data.getStart(), data.getEnd());
		addAllFromData(data);
	}





	public IntervalData build() {
		if (!isValid()) {
			throw new IllegalStateException("Interval Data object does not contain any interval: " + toString());
		}
		Timer timer = new Timer();
		sort(data.closings);
		sort(data.openings);
		logger.debug("Sorting time = " + timer.elapsedTime() + " ms");
		data.reads = data.openings.size();
		return data;
	}


	/*
	 * Intervals are identified by [start pos, end pos]
	 */
	public final IntervalDataBuilder addIntervalSE(int[] interval) {
		data.openings.add(interval[0]);
		data.closings.add(interval[1]);
		data.totalLenght += (interval[1] - interval[0]);
		return this;
	}


	/*
	 * Intervals are identified by [start pos, lenght]
	 */
	public final IntervalDataBuilder addIntervalSL(int[] interval) {
		data.openings.add(interval[0]);
		data.closings.add(interval[0] + interval[1]);
		data.totalLenght += (interval[1]);
		return this;
	}


	/*
	 * Intervals are identified by [start pos, end]
	 */
	public final IntervalDataBuilder addAllIntervalsSE(List<int[]> intervals) {
		for (int[] interval : intervals) {
			addIntervalSE(new int[]{interval[0], interval[1]});
		}
		return this;
	}


	public IntervalDataBuilder addAllFromData(IntervalData data) {
		this.data.openings.addAll(data.openings);
		this.data.closings.addAll(data.closings);
		this.data.totalLenght += data.totalLenght;
		return this;
	}


	public String getSampleName() {
		return data.getSampleName();
	}


	public String getReferenceName() {
		return data.getReferenceName();
	}


	public boolean isValid() {
		return !data.openings.isEmpty();
	}


	@Override
	public String toString() {
		if (isValid()) {
			return getSampleName() + ", " + getReferenceName() + " with " + data.openings.size() + " reads";
		}
		return getSampleName() + ", " + getReferenceName();
	}



	private void sort(List<Integer> list) {
		Collections.sort(list, new Comparator<Integer>() {
			@Override
			public int compare(Integer o1, Integer o2) {
				return o2.compareTo(o1);
			}
		});
	}

}
