package com.jalvz.bio.depthcover.reader;

import com.jalvz.bio.depthcover.model.hdata.LazyHeaderData;
import com.jalvz.bio.depthcover.model.idata.IntervalDataBuilder;

import net.sf.samtools.SAMRecord;
import net.sf.samtools.SAMRecordIterator;

/**
 * Depends on global state (LazyHeaderData)
 */
public class IntervalDataBuilderFactory {
	
	private static final LazyHeaderData head = LazyHeaderData.getInstance();
	
	public static IntervalDataBuilder newNullableDataBuilder(SAMRecordIterator recordIterator) {
	 	if (recordIterator.hasNext()) {
	 		return newSafeDataBuilder(recordIterator.next(), true);
    	}
	 	return null;
	}
	
	
	public static IntervalDataBuilder newSafeDataBuilder(SAMRecord record, boolean process) {
		String reference = record.getReferenceName();
		int refSize = new Long(head.get(reference)).intValue();
		IntervalDataBuilder builder = new IntervalDataBuilder(record.getReadGroup().getSample(), reference, 0, refSize);
		if (process) {
			SAMFileHandler.populateIntervalDataBuilder(builder, record);
		}
		return builder;
	}

}
