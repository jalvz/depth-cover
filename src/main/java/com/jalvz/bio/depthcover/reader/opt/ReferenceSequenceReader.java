package com.jalvz.bio.depthcover.reader.opt;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import net.sf.samtools.SAMSequenceRecord;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.jalvz.bio.depthcover.Tags;
import com.jalvz.bio.depthcover.reader.HelperReader;
import com.jalvz.bio.depthcover.reader.async.SafeAsyncReader;

public class ReferenceSequenceReader extends SafeAsyncReader<Map<String, Long>> {

	private final Map<String, Long> totalLocusCount;
	
	private final List<String> knownReferences;

	private final File fastaFile;
	
	public ReferenceSequenceReader(File fastaFile) {
		this.totalLocusCount = Maps.newHashMap();
		this.knownReferences = Lists.newArrayList();
		this.fastaFile = fastaFile;
		setupKnownReferences();
	}


	@Override
	public Map<String, Long> get() {
		forceComputation();
		return totalLocusCount;
	}
	
	
	@Override
	protected void read() throws IOException {
		initResource(fastaFile);
		processFasta();
	}
	
	
	private void processFasta() throws IOException {
		long totalCount = 0;
		String currentRef = null;
		long currentSkipCount = 0;
		String line = currentReaderObj.readLine();
		while (line != null) {
			if (line.startsWith(">")) {
				currentRef = refLookup(line);
				line = currentReaderObj.readLine();
				continue;
			}
			if (currentRef != null) {
				currentSkipCount += countSkips(line);
				long savedCount = totalLocusCount.containsKey(currentRef) ? 
						totalLocusCount.get(currentRef) : 0;
				totalCount += line.length() - currentSkipCount;
				totalLocusCount.put(currentRef, savedCount + line.length() - currentSkipCount);
			}
			currentSkipCount = 0;
			line = currentReaderObj.readLine();
		}
    	totalLocusCount.put(Tags.ALL, totalCount);
	}
	
	
	private long countSkips(String line) {
		long skipCount = 0;
		for (char base : line.toCharArray()) {
			if (base == 'N') {
				skipCount += 1;
			}
		}
		return skipCount;
	}
	
	private String refLookup(String comment) {
		for (String knownRef : knownReferences) {
			if (comment.contains(knownRef)) {
				return knownRef;
			}
		}
		return null;
	}
	

	private void setupKnownReferences() {
		for (SAMSequenceRecord record : HelperReader.getInstance().getReferences()) {
			knownReferences.add(record.getSequenceName());
		}
	}

}
