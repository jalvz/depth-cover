package com.jalvz.bio.depthcover.model.idata;

import gnu.trove.list.array.TIntArrayList;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;

import com.google.common.collect.Lists;
import com.jalvz.bio.depthcover.util.Timer;

public class IntervalData implements Iterable<int[]> {
	
	private static final Logger logger = Logger.getLogger(IntervalData.class.getName());



	protected final TIntArrayList openings;
	
	protected final TIntArrayList closings;
	
	protected long totalLenght;
	
	protected long reads;
	
	private final String sampleName;
	
	private final String referenceName;
	
	private final String originalReferenceName;


	private final int start;
	
	private final int end;
	
	
	
	public IntervalData(String sample, String referenceId, String referenceName, int start, int end) {
		this.openings = new TIntArrayList();
		this.closings = new TIntArrayList();
		this.sampleName = sample;
		this.referenceName = referenceId;
		this.originalReferenceName = referenceName;
		this.start = start;
		this.end = end;
		this.reads = 0;
		this.totalLenght = 0;
	}


	public TIntArrayList getOpenings() {
		return openings;
	}


	public TIntArrayList getClosings() {
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

	
	public long getTotalLenght() {
		return totalLenght;
	}


	public long getReads() {
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
				openings.removeAt(cidx + 1);
				closings.removeAt(cidx + 1);
			}
		}
		
	}


	/**
	 * This method needs to be called to ensure object consistency.
	 * Ideally, this should be handled in the Builder object.
	 * It is not for performance reasons.
	 */
	public void prepare() {
		Timer timer = new Timer();
		openings.sort();
		closings.sort();
		openings.reverse();
		closings.reverse();
		
		
		logger.trace("Sorting time = " + timer.elapsedTime() + " ms");
	}
	
	
}
