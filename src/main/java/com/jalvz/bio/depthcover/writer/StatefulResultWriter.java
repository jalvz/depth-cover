package com.jalvz.bio.depthcover.writer;

import static com.jalvz.bio.depthcover.model.hdata.LazyHeaderData.getAveragesHeadline;
import static com.jalvz.bio.depthcover.model.hdata.LazyHeaderData.getDetailsHeadline;
import static com.jalvz.bio.depthcover.model.hdata.LazyHeaderData.getGlobalHeadline;
import static com.jalvz.bio.depthcover.model.hdata.LazyHeaderData.getReferenceHeadline;
import static com.jalvz.bio.depthcover.util.WriterUtils.getProportion;
import static com.jalvz.bio.depthcover.util.WriterUtils.toStr;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import com.google.common.collect.Lists;
import com.jalvz.bio.depthcover.Tags;
import com.jalvz.bio.depthcover.model.hdata.LazyHeaderData;

/**
 * This class handles writes in a synchronous manner. 
 * It exposes an AsyncWriter component that needs to be managed by the user.
 * Depends on global state (LazyHeaderData)
 */
public class StatefulResultWriter {
	
	private static final Logger logger = Logger.getLogger(StatefulResultWriter.class.getName());

	private final DelegatedWriter summary;
	
	private final DelegatedWriter coverage;

	private final DelegatedWriter breakdown;

	private final DoubleBufferAsyncWriter details;
	
	private final long depthLimit;
	
	private final LazyHeaderData head;
	
	
	public StatefulResultWriter(File outDir, String prefix, boolean printDetails, long depthLimit) throws IOException {
		summary = new DelegatedWriter(new File(outDir, prefix + Tags.SUMMARY_EXT), getAveragesHeadline());
		coverage = new DelegatedWriter(new File(outDir, prefix + Tags.COVERAGE_EXT), getGlobalHeadline());
		breakdown = new DelegatedWriter(new File(outDir, prefix + Tags.BREAKDOWN_EXT), getReferenceHeadline());
		if (printDetails) {
			details = new DoubleBufferAsyncWriter(new File(outDir, prefix + Tags.DETAILS_EXT), getDetailsHeadline());
		} else {
			details = null;
		}
		this.head = LazyHeaderData.getInstance();
		this.depthLimit = depthLimit;
	}
	
	
	public void addToSummary(String sample, String label, long totalCover, long reads, String ref) {
		if (totalCover < depthLimit) {
			long totalBases = head.get(ref);
			double proportion = getProportion(totalBases, totalCover);
			summary.addLine(Lists.newArrayList(sample, label, toStr(reads), toStr(totalCover), toStr(totalBases), toStr(proportion)));
		}
	}
	
	
	public void addBreakdownCoverage(String sample, String label, Map<Integer, Long> data, String ref) {
		long totalBases = head.get(ref);
		for (int depth : data.keySet()) {
			if (depth < depthLimit) {
				double proportion = getProportion(totalBases, data.get(depth));
				breakdown.addLine(Lists.newArrayList(sample, label, toStr(depth), toStr(data.get(depth)), toStr(totalBases), toStr(proportion)));
			}
		}
	}
	
	
	public void addCoverage(String sample, Map<Integer, Long> data) {
		long totalBases = head.getTotal();
		for (int depth : data.keySet()) {
			double proportion = getProportion(totalBases, data.get(depth));
			coverage.addLine(Lists.newArrayList(sample, toStr(depth), toStr(data.get(depth)), toStr(totalBases), toStr(proportion)));
		}
	}
	
	
	public DoubleBufferAsyncWriter getAsyncWriter() {
		return details;
	}

	
	public void flush() {
		summary.write();
		coverage.write();
		breakdown.write();
	}

	
	public void finalise() {
		if (head.isEmpty()) {
			logger.warn("*** WARNING : No header data present ***");
		}
		flush();
		summary.close();
		coverage.close();
		breakdown.close();
		if (details != null) {
			details.finalise();
		}
	}
	
	
	public void overrideTotalBasesCount(Set<String> refs) {
		long total = 0;
		for (String ref : refs) {
			long bases =  head.get(ref);
			if (bases > 0) {
				total += bases;
			}
		}
		head.overrideTotal(total);
	}
	
	
	@Override
	public String toString() {
		return coverage.toString() + "\n\n\t\t* * *\n\n" + breakdown.toString();
	}




}
