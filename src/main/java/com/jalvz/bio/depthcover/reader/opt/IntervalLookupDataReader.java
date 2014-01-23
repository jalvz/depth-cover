package com.jalvz.bio.depthcover.reader.opt;

import java.io.File;
import java.io.IOException;

import com.jalvz.bio.depthcover.model.idata.lookup.IntervalLookupDataSet;
import com.jalvz.bio.depthcover.reader.async.SafeAsyncReader;

public class IntervalLookupDataReader extends SafeAsyncReader<IntervalLookupDataSet> {
	
	private final File bedFile;
	
	private final IntervalLookupDataSet intervalLookupDataSet;

	public IntervalLookupDataReader(File bedFile) {
		this.bedFile = bedFile;
		this.intervalLookupDataSet = new IntervalLookupDataSet();
	}

	
	@Override
	public IntervalLookupDataSet get() {
		forceComputation();
		return intervalLookupDataSet;
	}

	
	@Override
	protected void read() throws IOException {
		initResource(bedFile);
		processBed();
	}
	

	private void processBed() throws IOException {
		String line = currentReaderObj.readLine();
		while (line != null) {
			String[] rawInterval = line.split("\t");
			if (rawInterval.length == 3) {
				intervalLookupDataSet.add(rawInterval[0], getInt(rawInterval[1]), getInt(rawInterval[2]));
			} else if (rawInterval.length > 3) {
				intervalLookupDataSet.add(rawInterval[0],  getInt(rawInterval[1]), getInt(rawInterval[2]), rawInterval[3]);					
			}
			line = currentReaderObj.readLine();
		}
	}
	
	
	private int getInt(String str) {
		try {
			return Integer.parseInt(str);
		} catch (NumberFormatException e) {
			throw new IllegalArgumentException("Supplied BED file " + bedFile.getName() + " is not valid", e);
		}
	}
	
}
