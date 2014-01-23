package com.jalvz.bio.depthcover.reader;

import static com.jalvz.bio.depthcover.reader.SAMFileHandler.parseIntervals;

import java.io.File;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.sf.samtools.SAMRecord;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.jalvz.bio.depthcover.ds.queue.CloseableQueue;
import com.jalvz.bio.depthcover.model.idata.IntervalData;
import com.jalvz.bio.depthcover.model.idata.IntervalDataBuilder;
import com.jalvz.bio.depthcover.model.idata.lookup.IntervalLookupDataSet;

public class SkipableSequenceReader extends ContinuousSequenceReader {
	
	protected final IntervalLookupDataSet intervalLookupDataSet;

	
	public SkipableSequenceReader(CloseableQueue queue, File samFile, IntervalLookupDataSet intervalLookupDataSet) {
		super(queue, samFile);
		this.intervalLookupDataSet = intervalLookupDataSet;
	}


	@Override
	protected boolean mustSkip(SAMRecord record) {
		for (int[] interval : parseIntervals(record)) {
			if (intervalLookupDataSet.containsSL(record.getReferenceName(), interval)) {
				return false;
			}
		}
		return true;
	}

	
	@Override
	protected void transfer(final IntervalDataBuilder intervalDataBuilder) throws InterruptedException {
		List<IntervalData> intervalDataList = Lists.newArrayList();
		if (intervalDataBuilder.isValid()) {
			Iterator<int[]> intervalsIt = intervalDataBuilder.build().iterator();
			int[] current;
			while (intervalsIt.hasNext()) {
				current = intervalsIt.next();
				intervalsIt.remove();
				intervalDataList.addAll(intervalLookupDataSet.intersectionsSE(
						intervalDataBuilder.getSampleName(), intervalDataBuilder.getReferenceName(), new int[]{current[0], current[1] - 1}));
			}
			transferEachLookupRowData(intervalDataList);
		}
	}


	
	private void transferEachLookupRowData(List<IntervalData> intervalDataList) throws InterruptedException {
		for (IntervalDataBuilder intervalDataBuilder : reduce(intervalDataList)) {
			super.transfer(intervalDataBuilder);
		}
	}
	
	
	private Collection<IntervalDataBuilder> reduce(List<IntervalData> intervalDataList) {
		Map<String, IntervalDataBuilder> cluster = Maps.newHashMap();
		for (IntervalData idata : intervalDataList) {
			String ref = idata.getReferenceName();
			if (cluster.containsKey(ref)) {
				cluster.put(ref, cluster.get(ref).addAllFromData(idata));
			} else {
				cluster.put(ref, new IntervalDataBuilder(idata)); 
			}
		}
		return cluster.values();
	}

}
