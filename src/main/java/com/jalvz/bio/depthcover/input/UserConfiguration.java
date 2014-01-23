package com.jalvz.bio.depthcover.input;

import java.io.File;

public class UserConfiguration {
	
	private final boolean lowFlag;
	
	private final boolean printDetails;
	
	private final long depthLimit;
	
	private final String name;
	
	private final File bed;
	
	private final File fasta;

	public UserConfiguration(boolean lowFlag, boolean printDetails, long depthLimit, String name, File bed, File fasta) {
		this.lowFlag = lowFlag;
		this.printDetails = printDetails;
		this.depthLimit = depthLimit;
		this.name = name;
		this.bed = bed;
		this.fasta = fasta;
	}

	
	public boolean isLowFlag() {
		return lowFlag;
	}

	public boolean printDetails() {
		return printDetails;
	}
	
	public long getDepthLimit() {
		return depthLimit;
	}

	public String getName() {
		return name;
	}

	public File getIntervalsInput() {
		return bed;
	}

	public File getReferenceFile() {
		return fasta;
	}
	

}
