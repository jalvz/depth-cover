package com.jalvz.bio.depthcover.exec;

import java.io.File;
import java.util.Map;

import org.apache.log4j.Logger;

import com.jalvz.bio.depthcover.analyser.DepthOfCoverageAnalyser;
import com.jalvz.bio.depthcover.input.UserConfiguration;
import com.jalvz.bio.depthcover.model.hdata.LazyHeaderData;
import com.jalvz.bio.depthcover.model.idata.lookup.IntervalLookupDataSet;
import com.jalvz.bio.depthcover.reader.SequenceReader;
import com.jalvz.bio.depthcover.reader.async.SafeAsyncReader;
import com.jalvz.bio.depthcover.reader.opt.IntervalLookupDataReader;
import com.jalvz.bio.depthcover.writer.StatefulResultWriter;

/**
 * Inits the header and starts the producers and consumer
 */
public class ConcurrentExecutor {
	
	private static final Logger logger = Logger.getLogger(ConcurrentExecutor.class.getName());

	private final ExecutorStrategy factory;


	public ConcurrentExecutor(ExecutorStrategy factory) {
		this.factory = factory;
	}
	

	public void execute(UserConfiguration config) {
		startHeaderRead(config.getReferenceFile());
		startSequenceRead(config.isLowFlag(), config.getIntervalsInput());
		StatefulResultWriter writer = startWriter(config.printDetails(), config.getDepthLimit(), config.getName());
		startAndJoinAnalyser(writer);
		factory.close();
	}
	


	private void startAndJoinAnalyser(StatefulResultWriter writer) {
		DepthOfCoverageAnalyser analyser = factory.getSequenceAnalyser(writer);
		Thread t = start(analyser);
		try {
			t.join();
		} catch (InterruptedException e) {
			logger.debug(e.getMessage(), e);
		}
	}
	
	
	private StatefulResultWriter startWriter(boolean printDetails, long depthLimit, String name) {
		StatefulResultWriter writer = factory.getLazyResultWriter(name, printDetails, depthLimit);
		if (printDetails) {
			start(writer.getAsyncWriter());
		}
		return writer;
	}
	
	
	private void startHeaderRead(File refFile) {
		SafeAsyncReader<Map<String, Long>> headReader = factory.getHeadSequenceReader(refFile);
		start(headReader);
		LazyHeaderData.init(headReader);
	}

	
	private void startSequenceRead(boolean forceSingleRead, File intervalsFile) {
		SequenceReader seqReader;
		if (intervalsFile != null) {
			IntervalLookupDataSet intervalsToRead = startIntervalLookupRead(intervalsFile).get();
			seqReader = factory.getPartialSequenceReader(intervalsToRead, forceSingleRead);
		} else {
			seqReader = factory.getSequenceReader(forceSingleRead);
		}
		start(seqReader);
	}

		
	private IntervalLookupDataReader startIntervalLookupRead(File intervalsFile) {
		IntervalLookupDataReader intervalReader = factory.getIntervalLookupReader(intervalsFile);
		start(intervalReader);
		return intervalReader;
	}
	
	
	private Thread start(Runnable runnable) {
		Thread thread = new Thread(runnable);
		thread.start();
		return thread;
	}
}
