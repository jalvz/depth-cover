package com.jalvz.bio.depthcover.reader;


import java.io.File;

import org.apache.log4j.Logger;

import net.sf.samtools.SAMRecord;
import net.sf.samtools.SAMRecordIterator;

import com.jalvz.bio.depthcover.ExitStatus;
import com.jalvz.bio.depthcover.ds.queue.CloseableQueue;
import com.jalvz.bio.depthcover.model.idata.IntervalDataBuilder;

public abstract class SequenceReader implements Runnable {
	
	private static final Logger logger = Logger.getLogger(SequenceReader.class.getName());
	
	protected final File samFile;
	
	private final CloseableQueue queue;
	
	
	protected SequenceReader(final CloseableQueue queue, File samFile) {
		this.queue = queue;
		this.samFile = samFile;
	}

	
	@Override
	public void run() {
		logger.debug("Started BAM Reader thread - Id = " + Thread.currentThread().getId());
		try {
			read();
		} catch (Throwable e) {
			logger.error(e.getMessage());
			ExitStatus.setStatus(1);
		} finally {
	    	queue.close();
		}
		logger.debug("Finished BAM Reader thread - Id = " + Thread.currentThread().getId());
	}
	
	
	protected abstract void read() throws Exception;
	
	
	protected void transfer(IntervalDataBuilder intervalDataBuilder) throws InterruptedException {
		if (intervalDataBuilder.isValid()) {
			logger.info("Read reference " + intervalDataBuilder.toString());
			queue.put(intervalDataBuilder);
		}
	}
	
	
	protected boolean readReference(final SAMRecordIterator recordIterator) throws InterruptedException {
		IntervalDataBuilder intervalDataBuilder = IntervalDataBuilderFactory.newNullableDataBuilder(recordIterator);
		if (intervalDataBuilder != null) {
			processReference(recordIterator, intervalDataBuilder);
			return true;
		}
		return false;
	}

	/**
	 * @param intervalDataBuilder mutates this argument
	 * @return first record in next sequence (or null if last sequence was processed)
	 * @throws InterruptedException 
	 */
	protected SAMRecord processReference(final SAMRecordIterator recordIterator, final IntervalDataBuilder intervalDataBuilder) throws InterruptedException {
		SAMRecord currentRecord = null; 
		while (recordIterator.hasNext()) {
			currentRecord = recordIterator.next();
			if (mustReturn(intervalDataBuilder, currentRecord)) {
				return currentRecord;
			} else if (mustSkip(currentRecord)) {
				continue;
			} else {
				SAMFileHandler.populateIntervalDataBuilder(intervalDataBuilder, currentRecord);
			}
		}
		try {	
			transfer(intervalDataBuilder);
		} catch (InterruptedException e) {
			logger.error(e.getMessage(), e);
		}
		return null;
	}
	
	
	protected boolean mustReturn(IntervalDataBuilder intervalDataBuilder, SAMRecord currentRecord) throws InterruptedException {
		return false;
	}

	
	protected boolean mustSkip(SAMRecord record) {
		return false;
	}

	
}
