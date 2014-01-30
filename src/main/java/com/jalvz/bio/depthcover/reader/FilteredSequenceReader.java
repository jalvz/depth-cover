package com.jalvz.bio.depthcover.reader;

import java.io.File;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;

import net.sf.samtools.SAMFileReader;
import net.sf.samtools.SAMRecordIterator;
import net.sf.samtools.SAMSequenceRecord;

import com.google.common.collect.Lists;
import com.jalvz.bio.depthcover.ds.queue.CloseableQueue;
import com.jalvz.bio.depthcover.model.idata.IntervalDataBuilder;
import com.jalvz.bio.depthcover.model.idata.lookup.IntervalLookupData;
import com.jalvz.bio.depthcover.model.idata.lookup.IntervalLookupDataSet;

public class FilteredSequenceReader extends SequenceReader {

	private static final Logger logger = Logger.getLogger(FilteredSequenceReader.class.getName());
	
	private final IntervalLookupDataSet intervalLookupDataSet;
	
	// State vars
	private IntervalLookupData currentIntervalLookupData = null;

	public FilteredSequenceReader(CloseableQueue queue, File samFile, IntervalLookupDataSet intervalLookupDataSet) {
		super(queue, samFile);
		this.intervalLookupDataSet = intervalLookupDataSet;
	}

	
	@Override
	protected void read() throws InterruptedException {
		SAMFileReader samFileReader = new SAMFileReader(samFile);
		for (SAMSequenceRecord chunk : HelperReader.getInstance().getReferences()) {
			logger.trace("Started to read reference " + chunk.getSequenceName());
			Set<IntervalLookupData> intervalDataSet = intervalLookupDataSet.getReferenceIntervalData(chunk.getSequenceName());
			for (IntervalLookupData intervalData : intervalDataSet) {
				updateState(intervalData);
				SAMRecordIterator iterator = samFileReader.queryOverlapping(
						chunk.getSequenceName(), intervalData.getStart(), intervalData.getEnd());
				boolean success = readReference(iterator);
				if (!success) {
					clearState();
				}
				iterator.close();
			}
		}
		samFileReader.close();
	}
	

	@Override
	protected void transfer(IntervalDataBuilder intervalDataBuilder) throws InterruptedException {
		List<int[]> shrinkedIntervals = Lists.newArrayList();
		if (intervalDataBuilder.isValid()) {
			Iterator<int[]> intervalsIt = intervalDataBuilder.build().iterator();
			int[] current;
			while (intervalsIt.hasNext()) {
				current = intervalsIt.next();
				intervalsIt.remove();
				try {
				shrinkedIntervals.add(intervalLookupDataSet.intersectionSE(new int[]{current[0], current[1] - 1}, currentIntervalLookupData.getInterval()));
				} catch (IllegalArgumentException e) {
					logger.debug(e.getMessage(), e);
				}
			}
			super.transfer(new IntervalDataBuilder(
					intervalDataBuilder.getSampleName(), currentIntervalLookupData.getId(), currentIntervalLookupData.getReference(), currentIntervalLookupData.getStart(), currentIntervalLookupData.getEnd()).
					addAllIntervalsSE(shrinkedIntervals));
		}
		clearState();
	}

	
	private void updateState(IntervalLookupData iData) {
		if (currentIntervalLookupData != null) {
			throw new UnsupportedOperationException("State can not be updated until it is consumed");
		}
		currentIntervalLookupData = iData;
	}
	

	private void clearState() {
		currentIntervalLookupData = null;
	}
	
	
}
