package com.jalvz.bio.depthcover.reader.opt;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import net.sf.samtools.SAMSequenceRecord;

import com.jalvz.bio.depthcover.Tags;
import com.jalvz.bio.depthcover.reader.HelperReader;
import com.jalvz.bio.depthcover.reader.async.SafeAsyncReader;

public class HeadSequenceReader extends SafeAsyncReader<Map<String, Long>> {

	private static final Logger logger = Logger.getLogger(HeadSequenceReader.class.getName());
	
	private final Map<String, Long> totalLocusCount;

	public HeadSequenceReader() {
		this.totalLocusCount = new HashMap<String, Long>();
	}


	@Override
	public Map<String, Long> get() {
		forceComputation();
		return totalLocusCount;
	}
	
	
	@Override
	protected void read() {
		long totalCount = 0;
    	for (SAMSequenceRecord chunk : HelperReader.getInstance().getReferences()) {
    		String refName = chunk.getSequenceName();
    		long currentCount = totalLocusCount.containsKey(refName) ? totalLocusCount.get(refName) : 0;
    		currentCount += chunk.getSequenceLength();
    		totalCount += currentCount;
    		totalLocusCount.put(refName, currentCount);
    	}
    	totalLocusCount.put(Tags.ALL, totalCount);
    	logger.debug("HEADER DATA *** \n" + totalLocusCount);
	}

}
