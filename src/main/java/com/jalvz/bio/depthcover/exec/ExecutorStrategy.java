package com.jalvz.bio.depthcover.exec;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import org.apache.log4j.Logger;

import com.google.common.annotations.VisibleForTesting;
import com.jalvz.bio.depthcover.analyser.DepthOfCoverageAnalyser;
import com.jalvz.bio.depthcover.ds.queue.CloseableQueue;
import com.jalvz.bio.depthcover.model.idata.lookup.IntervalLookupDataSet;
import com.jalvz.bio.depthcover.reader.ContinuousSequenceReader;
import com.jalvz.bio.depthcover.reader.FilteredSequenceReader;
import com.jalvz.bio.depthcover.reader.HelperReader;
import com.jalvz.bio.depthcover.reader.ParallelSequenceReader;
import com.jalvz.bio.depthcover.reader.SequenceReader;
import com.jalvz.bio.depthcover.reader.SkipableSequenceReader;
import com.jalvz.bio.depthcover.reader.async.SafeAsyncReader;
import com.jalvz.bio.depthcover.reader.opt.HeadSequenceReader;
import com.jalvz.bio.depthcover.reader.opt.IntervalLookupDataReader;
import com.jalvz.bio.depthcover.reader.opt.ReferenceSequenceReader;
import com.jalvz.bio.depthcover.writer.StatefulResultWriter;

public class ExecutorStrategy {
	
	private static final Logger logger = Logger.getLogger(ExecutorStrategy.class.getName());

	private static final int QUEUE_SIZE = 3;
		
	private final CloseableQueue queue;
	
	private final File samFile;
	
	private final File outDir;

	private final boolean hasIndex;
	
	private static final long FILE_SIZE_THRESHOLD = 4000000000L; // 4GB
	
	
	public ExecutorStrategy(File sam, File outDir) {
		HelperReader.init(sam);
		this.hasIndex = HelperReader.getInstance().hasIndex();
		this.queue = new CloseableQueue(QUEUE_SIZE);
		this.samFile = sam;
		this.outDir = outDir;
	}

	
	public StatefulResultWriter getLazyResultWriter(String name, boolean printDetails, long depthLimit) {
		try {
			return new StatefulResultWriter(outDir, name, printDetails, depthLimit);
		} catch (IOException e) {
			logger.debug(e.getMessage(), e);
			return null;
		}
	}
	
	
	public DepthOfCoverageAnalyser getSequenceAnalyser(StatefulResultWriter writer) {
		return new DepthOfCoverageAnalyser(queue, writer);
	}
	
	
	public SafeAsyncReader<Map<String, Long>> getHeadSequenceReader(File refFile) {
		if (refFile == null) {
			return new HeadSequenceReader();
		}
		return new ReferenceSequenceReader(refFile);
	}
	
	
	public IntervalLookupDataReader getIntervalLookupReader(File bedFile) {
		return new IntervalLookupDataReader(bedFile);
	}

		
	public SequenceReader getSequenceReader(boolean forceSingleRead) {
		if (parallelizable() && !forceSingleRead) {
			logger.info("Index found - Using Parallel Reader.");
			return new ParallelSequenceReader(queue, samFile);
		}
		return new ContinuousSequenceReader(queue, samFile);
	}

	
	public SequenceReader getPartialSequenceReader(IntervalLookupDataSet intervalLookupDataSet) {
		if (hasIndex) {
			logger.info("Index found.");
			return new FilteredSequenceReader(queue, samFile, intervalLookupDataSet);
		} 
		return new SkipableSequenceReader(queue, samFile, intervalLookupDataSet);
	}


	public void close() {
		HelperReader.getInstance().close();
	}
	

	private boolean parallelizable() {
		return hasIndex && fileIsBig(samFile);
	}


	@VisibleForTesting
	protected boolean fileIsBig(File file) {
		return file.length() > FILE_SIZE_THRESHOLD;
	}
	

}
