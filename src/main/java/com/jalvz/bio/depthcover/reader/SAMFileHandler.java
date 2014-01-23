package com.jalvz.bio.depthcover.reader;


import java.util.List;

import net.sf.samtools.AlignmentBlock;
import net.sf.samtools.SAMRecord;

import com.google.common.collect.Lists;
import com.jalvz.bio.depthcover.model.idata.IntervalDataBuilder;

public class SAMFileHandler {

	
	protected static List<int[]> parseIntervals(SAMRecord record) {
		List<int[]> intervals = Lists.newArrayList();
		for (AlignmentBlock seqSubgroup : record.getAlignmentBlocks()) {
			intervals.add(new int[]{seqSubgroup.getReferenceStart() + seqSubgroup.getReadStart(), seqSubgroup.getLength()});
		}
		return intervals;
	}
	
	
	public static void populateIntervalDataBuilder(final IntervalDataBuilder builder, SAMRecord currentRecord) {
		if (isAligned(currentRecord)) {
			for (int[] interval : parseIntervals(currentRecord)) {
				builder.addIntervalSL(interval);
			}
		}
	}
	
	
	private static boolean isAligned(SAMRecord record) {
		return !record.getReadUnmappedFlag();
	}
	
}
