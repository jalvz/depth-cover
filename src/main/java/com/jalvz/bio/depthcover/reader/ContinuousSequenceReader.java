package com.jalvz.bio.depthcover.reader;

import java.io.File;

import org.apache.log4j.Logger;

import net.sf.samtools.SAMFileReader;
import net.sf.samtools.SAMRecord;
import net.sf.samtools.SAMRecordIterator;

import com.jalvz.bio.depthcover.ds.queue.CloseableQueue;
import com.jalvz.bio.depthcover.model.idata.IntervalDataBuilder;

public class ContinuousSequenceReader extends SequenceReader {

	
	public ContinuousSequenceReader(CloseableQueue queue, File samFile) {
		super(queue, samFile);
	}

	
	@Override
	protected void read() throws InterruptedException {
		SAMFileReader samFileReader = new SAMFileReader(samFile);
		SAMRecordIterator recordIterator = samFileReader.iterator();
		IntervalDataBuilder dataBuilder = IntervalDataBuilderFactory.newNullableDataBuilder(recordIterator);
		if (dataBuilder != null) {
			SAMRecord firstInNextReference = processReference(recordIterator, dataBuilder);
			while (firstInNextReference != null) {
				firstInNextReference = processReference(recordIterator, 
						IntervalDataBuilderFactory.newSafeDataBuilder(firstInNextReference, !mustSkip(firstInNextReference)));
			}
		}
		samFileReader.close();
	}


	@Override
	protected boolean mustReturn(final IntervalDataBuilder intervalDataBuilder, SAMRecord record) throws InterruptedException {
		if (newReferenceJustStarted(intervalDataBuilder, record)) {
			transfer(intervalDataBuilder);
			return true;
		}
		return false;
	}

	
	private boolean newReferenceJustStarted(final IntervalDataBuilder intervalDataBuilder, final SAMRecord currentRecord) {
		boolean sameSample = intervalDataBuilder.getSampleName().equals(currentRecord.getReadGroup().getSample());
		boolean sameReference = intervalDataBuilder.getReferenceName().equals(currentRecord.getReferenceName());
		return !sameSample || !sameReference;
	}

}
