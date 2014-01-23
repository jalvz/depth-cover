package com.jalvz.bio.depthcover.model.idata.lookup;



public class IntervalLookupData {
	
	private final String reference;
	
	private final int start;
	
	private final int end;
	
	private final String id;

	
	
	protected IntervalLookupData(String reference, String label, int start, int end) {
		this.reference = reference;
		this.start = start;
		this.end = end;
		if (label != null) {
			this.id = reference + " " + start + "-" + end + " (" + label + ")";
		} else {
			this.id = reference + " " + start + "-" + end;
		}
	}

	
	
	public String getId() {
		return id;
	}


	public String getReference() {
		return reference;
	}

	
	public Integer getStart() {
		return start;
	}
	
	
	public Integer getEnd() {
		return end;
	}


	public int[] getInterval() {
		return new int[]{start,end};
	}
	
}
