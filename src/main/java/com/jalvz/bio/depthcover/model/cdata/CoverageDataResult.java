package com.jalvz.bio.depthcover.model.cdata;


import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.jalvz.bio.depthcover.model.cdata.CoverageDataResultHelper.*;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.jalvz.bio.depthcover.Tags;
import com.jalvz.bio.depthcover.writer.StatefulResultWriter;

public class CoverageDataResult {
	
	private final List<CoverageData> rawCoverageResults;

	
	public CoverageDataResult() {
		this.rawCoverageResults = Lists.newArrayList();
	}

    
	public void set(CoverageData data) {
		rawCoverageResults.add(data);
	}
	

	public boolean isEmpty() {
		return rawCoverageResults.isEmpty();
	}
	
		
	public void populateWriter(final StatefulResultWriter writer) {
		writer.overrideTotalBasesCount(uniqueRefs());
		addSummaryAll(writer);
		for (CoverageData coverageData : rawCoverageResults) {
			writer.addToSummary(coverageData.getSampleName(), coverageData.getReferenceName(), 
					coverageData.getTotalCoverage(), coverageData.getReads(), coverageData.getOriginalReferenceName());
			writer.addBreakdownCoverage(coverageData.getSampleName(), coverageData.getReferenceName(), 
					accumulateOnPreviousValues(coverageData.getCoverage()), coverageData.getOriginalReferenceName());
			writer.flush();
		}
	}
	
	
	private void addSummaryAll(final StatefulResultWriter writer) {
		List<Map<Integer, Long>> breakdownResults = Lists.newArrayList();
		Set<String> uniqueSamples = Sets.newHashSet();
		long overallMeanCoverage = 0;
		long overallReads = 0;
		for (CoverageData coverageData : rawCoverageResults) {
			uniqueSamples.add(coverageData.getSampleName());
			breakdownResults.add(coverageData.getCoverage());
			overallMeanCoverage += coverageData.getTotalCoverage();
			overallReads += coverageData.getReads();
		}
		String samplesStr = samplesLabel(uniqueSamples);
		writer.addToSummary(samplesStr, Tags.ALL, overallMeanCoverage, overallReads, Tags.ALL);
		writer.addCoverage(samplesStr, merge(breakdownResults));
		writer.flush();
	}
	
	
	
	private Set<String> uniqueRefs() {
		Set<String> refs = Sets.newTreeSet();
		for (CoverageData coverageData : rawCoverageResults) {
			refs.add(coverageData.getOriginalReferenceName());
		}
		return refs;
	}
	
	
	private String samplesLabel(Set<String> samples) {
		String samplesStr = "";
		for (String sample : samples) {
			samplesStr = samplesStr + sample + " ";
		}
		return samplesStr.trim();
	}
}
