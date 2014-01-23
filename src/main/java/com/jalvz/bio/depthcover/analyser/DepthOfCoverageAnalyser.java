package com.jalvz.bio.depthcover.analyser;



import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

import com.google.common.annotations.VisibleForTesting;
import com.jalvz.bio.depthcover.ExitStatus;
import com.jalvz.bio.depthcover.analyser.calc.CoverageCalculator;
import com.jalvz.bio.depthcover.analyser.calc.StandardCoverageCalculator;
import com.jalvz.bio.depthcover.analyser.calc.StepCoverageCalculator;
import com.jalvz.bio.depthcover.ds.queue.CloseableQueue;
import com.jalvz.bio.depthcover.model.cdata.CoverageDataResult;
import com.jalvz.bio.depthcover.model.idata.IntervalDataBuilder;
import com.jalvz.bio.depthcover.writer.StatefulResultWriter;


public class DepthOfCoverageAnalyser implements Runnable {

	private static final Logger logger = Logger.getLogger(DepthOfCoverageAnalyser.class.getName());

	private final CloseableQueue queue;

	private final CoverageCalculator coverage;
	
	private final StatefulResultWriter writer;
	
	private final CoverageDataResult result = new CoverageDataResult();
	
	
	public DepthOfCoverageAnalyser(CloseableQueue queue, StatefulResultWriter writer) {
		this.queue = queue;
		this.writer = writer;
		if (writer.getAsyncWriter() != null) {
			this.coverage = new StepCoverageCalculator(writer.getAsyncWriter());
		} else {
			this.coverage = new StandardCoverageCalculator();
		}
	}


	@Override
	public void run() {
		logger.debug("Started Analyser thread - Id = " + Thread.currentThread().getId());
		process();
		if (!result.isEmpty()) {
			result.populateWriter(writer);
		} else {
			logger.info("No Data to write - BAM file seems to be empty or coverage = 0");
		}
		writer.finalise();
		logger.debug("Finished Analyser thread - Id = " + Thread.currentThread().getId());
	}


	private void process() {
		while (queue.notFinished()) {
			try {
				IntervalDataBuilder idata = queue.poll(3,TimeUnit.SECONDS);
				if (idata != null) {
					result.set(coverage.calculate(idata.build()));
				}
			} catch (Throwable e) {
				logger.info(e.getMessage(), e);
				ExitStatus.setStatus(1);
				queue.close();
				return;
			}
		}
	}
	
	
	@VisibleForTesting
	public CoverageCalculator getCalculator() {
		return coverage;
	}
}
