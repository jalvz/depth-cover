package com.jalvz.bio.depthcover.model.idata;

import java.util.Iterator;
import java.util.List;

import com.google.common.collect.Lists;

public class IntervalData implements Iterable<int[]> {

	protected final List<Integer> openings;
	
	protected final List<Integer> closings;
	
	protected int totalLenght;
	
	protected int reads;
	
	private final String sampleName;
	
	private final String referenceName;
	
	private final String originalReferenceName;


	private final int start;
	
	private final int end;
	
	
	
	public IntervalData(String sample, String referenceId, String referenceName, int start, int end) {
		this.openings = Lists.newArrayList();
		this.closings = Lists.newArrayList();
		this.sampleName = sample;
		this.referenceName = referenceId;
		this.originalReferenceName = referenceName;
		this.start = start;
		this.end = end;
		this.reads = 0;
		this.totalLenght = 0;
	}


	public List<Integer> getOpenings() {
		return openings;
	}


	public List<Integer> getClosings() {
		return closings;
	}


	public String getSampleName() {
		return sampleName;
	}

	
	public String getReferenceName() {
		return referenceName;
	}

	
	public String getOriginalReferenceName() {
		return originalReferenceName;
	}


	public int getStart() {
		return start;
	}


	public int getEnd() {
		return end;
	}

	
	public int getTotalLenght() {
		return totalLenght;
	}


	public int getReads() {
		return reads;
	}


	/**
	 * Iterates in reverse order to allow fast removes	
	 */
	@Override
	public Iterator<int[]> iterator() {		
		return new IntervalDataIterator();
	}
	
	
	
	class IntervalDataIterator implements Iterator<int[]> {
		
		private int cidx = openings.size() - 1;
		
		@Override
		public boolean hasNext() {
			return cidx >= 0;
		}

		@Override
		public int[] next() {
			int[] interval = new int[]{openings.get(cidx), closings.get(cidx)};
			cidx--;
			return interval;
		}

		@Override
		public void remove() {
			if (cidx < openings.size() - 1) {
				openings.remove(cidx + 1);
				closings.remove(cidx + 1);
			}
		}
		
	}
	
}
