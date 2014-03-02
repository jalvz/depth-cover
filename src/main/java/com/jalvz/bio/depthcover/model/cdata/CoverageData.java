package com.jalvz.bio.depthcover.model.cdata;


import java.util.HashMap;
import java.util.Map;

public class CoverageData {
	
	
	private final Map<Integer, Long> coverage;
	
	private final String sampleName, referenceName, originalReferenceName;
	
	private final long reads, totalCoverage;

	
	public CoverageData(Map<Integer, Long> coverage, String sampleName, String referenceName, String originalName, long totalCoverage, long reads) {
		this.coverage = CoverageDataResultHelper.newMap();
		this.coverage.putAll(coverage);
		this.sampleName = sampleName;
		this.referenceName = referenceName;
		this.originalReferenceName = originalName;
		this.reads = reads;
		this.totalCoverage = totalCoverage;
	}
	
	
	public CoverageData(String sampleName, String referenceName, String originalName) {
		this(new HashMap<Integer, Long>(), sampleName, referenceName, originalName, 0, 0);
	}
	
	
	public Map<Integer, Long> getCoverage() {
		return coverage;
	}
	
		
	public String getSampleName() {
		return sampleName;
	}

	
	public String getReferenceName() {
		return referenceName;
	}


	public String getOriginalReferenceName() {
		return originalReferenceName;
	}


	public long getReads() {
		return reads;
	}


	public long getTotalCoverage() {
		return totalCoverage;
	}
	

}
