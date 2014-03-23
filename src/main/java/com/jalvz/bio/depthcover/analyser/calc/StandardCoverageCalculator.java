package com.jalvz.bio.depthcover.analyser.calc;

import gnu.trove.list.array.TIntArrayList;

import java.util.Map;

import org.apache.log4j.Logger;

import com.google.common.collect.Maps;
import com.jalvz.bio.depthcover.model.cdata.CoverageData;
import com.jalvz.bio.depthcover.model.idata.IntervalData;
import com.jalvz.bio.depthcover.util.Timer;

public class StandardCoverageCalculator implements CoverageCalculator {

	private static final Logger logger = Logger
			.getLogger(StandardCoverageCalculator.class.getName());

	@Override
	public CoverageData calculate(IntervalData intervalData) {
		if (intervalData.iterator().hasNext()) {
			return new CoverageData(internalCalculate(intervalData),
					intervalData.getSampleName(),
					intervalData.getReferenceName(),
					intervalData.getOriginalReferenceName(),
					intervalData.getTotalLenght(),
					intervalData.getReads());
		}
		return new CoverageData(intervalData.getSampleName(),
				intervalData.getReferenceName(),
				intervalData.getOriginalReferenceName());
	}

	
	private Map<Integer, Long> internalCalculate(IntervalData intervalData) {
		Timer timer = new Timer();
		Map<Integer, Long> countMap = Maps.newHashMap();

		int[][] intervalEvents = build(intervalData.getOpenings(), intervalData.getClosings());
		int currentSpanPosition = intervalEvents[0][0];
		send(intervalData.getReferenceName(), 0, intervalData.getStart(), currentSpanPosition);
		int depthState = intervalEvents[0][1];
		long count;
		for (int cidx = 1; cidx < intervalEvents.length; cidx++) {
			int nextSpanPosition = intervalEvents[cidx][0];
			if (nextSpanPosition > -1) {
				count = countMap.containsKey(depthState) ? countMap.get(depthState) : 0;
				if (depthState > 0) {
					countMap.put(depthState, count + nextSpanPosition - currentSpanPosition);
				}
				send(intervalData.getReferenceName(), depthState, currentSpanPosition, nextSpanPosition);
				depthState -= intervalEvents[cidx][2];
				depthState += intervalEvents[cidx][1];
				currentSpanPosition = nextSpanPosition;
			}
		}
		send(intervalData.getReferenceName(), 0, currentSpanPosition, intervalData.getEnd());
		logger.debug("Calculation time = " + timer.elapsedTime() + " ms");
		return countMap;
	}

	protected void send(String ref, int state, int from, int to) {
		return;
	}

	/**
	 * Spots where 'events' ocurr (start/end positions of sequences)
	 * 
	 * @param openings
	 *            sorted list of opening positions (included, i.e. first
	 *            position of the interval)
	 * @param closings
	 *            sorted list of closing positions (excluded, i.e. first
	 *            position after the end of the interval)
	 * @return array of [position, number of sequences starting at pos, number
	 *         of sequences finished at pos] sorted by pos
	 */
	private int[][] build(TIntArrayList openings, TIntArrayList closings) {
		Timer timer = new Timer();
		int[][] sortedEvents = init(openings.size() + closings.size());
		int cidx = 0;
		while (openings.size() > 0) {
			if (last(closings) > last(openings)) {
				cidx = updateOpening(sortedEvents, removeLast(openings), cidx);
			} else if (last(closings) < last(openings)) {
				cidx = updateClosing(sortedEvents, removeLast(closings), cidx);
			} else {
				removeLast(openings);
				cidx = updateBoth(sortedEvents, removeLast(closings), cidx);
			}
		}
		while (closings.size() > 0) {
			cidx = updateClosing(sortedEvents, removeLast(closings), cidx);
		}
		logger.debug("Build time = " + timer.elapsedTime() + " ms");
		return sortedEvents;
	}

	private int[][] init(int size) {
		int[][] sortedEvents = new int[size][3];
		for (int cidx = 0; cidx < sortedEvents.length; cidx++) {
			sortedEvents[cidx][0] = -1;
		}
		return sortedEvents;
	}

	private int updateOpening(final int[][] sortedEvents, int position, int cidx) {
		return update(sortedEvents, position, cidx, 1);
	}

	private int updateClosing(final int[][] sortedEvents, int position, int cidx) {
		return update(sortedEvents, position, cidx, 2);
	}

	private int updateBoth(final int[][] sortedEvents, int position, int cidx) {
		update(sortedEvents, position, cidx, 1);
		return update(sortedEvents, position, cidx, 2);
	}

	private int update(final int[][] sortedEvents, int position, int cidx,
			int dimension) {
		if (cidx > 0 && sortedEvents[cidx - 1][0] == position) {
			sortedEvents[cidx - 1][dimension] += 1;
		} else {
			sortedEvents[cidx][0] = position;
			sortedEvents[cidx][dimension] += 1;
			cidx++;
		}
		return cidx;
	}

	private int last(TIntArrayList list) {
		return list.get(list.size() - 1);
	}

	private int removeLast(final TIntArrayList list) {
		return list.removeAt(list.size() - 1);
	}

}
