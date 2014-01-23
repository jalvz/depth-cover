package com.jalvz.bio.depthcover.reader;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.apache.log4j.Logger;

import net.sf.samtools.SAMFileReader;
import net.sf.samtools.SAMRecordIterator;
import net.sf.samtools.SAMSequenceRecord;

import com.jalvz.bio.depthcover.ds.queue.CloseableQueue;
import com.jalvz.bio.depthcover.ds.queue.QueueBuffer;
import com.jalvz.bio.depthcover.model.idata.IntervalDataBuilder;

public class ParallelSequenceReader extends SequenceReader {

	private static final Logger logger = Logger.getLogger(ParallelSequenceReader.class.getName());
	
	private static final int MAX_THREADS = 4;
	
	private final ExecutorService executor = Executors.newFixedThreadPool(Math.min(MAX_THREADS, Runtime.getRuntime().availableProcessors()));

	private final QueueBuffer buffer;
	
	public ParallelSequenceReader(CloseableQueue queue, File samFile) {
		super(queue, samFile);
		buffer = new QueueBuffer(queue, HelperReader.getInstance().getReferences());
	}


	@Override
	protected void read() {
		List<Future<SAMRecordIterator>> futures = new ArrayList<Future<SAMRecordIterator>>();
		// NOTE: samtools only allows one iterator per file at a time 
		List<SAMFileReader> fileReaders = new ArrayList<SAMFileReader>(); 
		SAMFileReader headFileReader = new SAMFileReader(samFile);
		fileReaders.add(headFileReader);
		int refIndex, refSize;
		for (SAMSequenceRecord chunk : HelperReader.getInstance().getReferences()) {
			refIndex = chunk.getSequenceIndex();
			refSize = chunk.getSequenceLength();
			SAMFileReader chunkFileReader = new SAMFileReader(samFile);
			Callable<SAMRecordIterator> task = getTask(chunkFileReader.iterator(chunkFileReader.getIndex().getSpanOverlapping(refIndex, 0, refSize)));
			fileReaders.add(chunkFileReader);
			futures.add(executor.submit(task));
		}
		forceComputation(futures, fileReaders);
	}
	
	
	@Override
	protected void transfer(IntervalDataBuilder intervalDataBuilder) throws InterruptedException {
		if (intervalDataBuilder.isValid()) {
			logger.info("Read reference " + intervalDataBuilder.toString());
			buffer.add(intervalDataBuilder);
		}
	}


	
	private Callable<SAMRecordIterator> getTask(final SAMRecordIterator recordIterator) {
		return new Callable<SAMRecordIterator>() {
			@Override
			public SAMRecordIterator call() throws InterruptedException {
				readReference(recordIterator);
				return recordIterator;
			}
		};
	}


	private void forceComputation(List<Future<SAMRecordIterator>> futures, List<SAMFileReader> fileReaders) {
		try {
			for (Future<SAMRecordIterator> future : futures) {
				SAMRecordIterator it = future.get();
				it.close();
			}
			for (SAMFileReader reader : fileReaders) {
				reader.close();
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new UnsupportedOperationException("Error reading BAM file. Try with --ignore-index");
		} finally {
			executor.shutdownNow();
		}
	}

}
