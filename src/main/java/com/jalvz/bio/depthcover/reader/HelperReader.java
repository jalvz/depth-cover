package com.jalvz.bio.depthcover.reader;

import java.io.File;
import java.util.List;

import org.apache.log4j.Logger;

import net.sf.samtools.SAMFileHeader;
import net.sf.samtools.SAMFileReader;
import net.sf.samtools.SAMSequenceRecord;

import com.google.common.collect.Lists;
import com.jalvz.bio.depthcover.util.Timer;

/*
 * Contains a global reference to the sequence dictionary  and index in the BAM.
 * Purpose for this being singleton is resource contention.
 */
public class HelperReader {
	
	private static final Logger logger = Logger.getLogger(HelperReader.class.getName());

	private static HelperReader instance;
	
	private final SAMFileReader reader;
	
	private final List<SAMSequenceRecord> references;
	
	private final boolean hasIndex;
	
	private HelperReader(SAMFileReader reader) {
		this.reader = reader;
		this.hasIndex = this.reader.hasIndex();
		this.references = Lists.newArrayList();
		this.references.addAll(this.reader.getFileHeader().getSequenceDictionary().getSequences());
	}
	
	
	/**
	 * Validates the order
	 */
	public static void init(File samFile) {
		SAMFileReader samFileReader = new SAMFileReader(samFile);
		boolean isOrdered =  samFileReader.getFileHeader().getSortOrder().equals(SAMFileHeader.SortOrder.coordinate);
		if (!isOrdered) {
			samFileReader.close();
			throw new IllegalArgumentException("SAM File " + samFile.getAbsolutePath() + " is not ordered.");
		}
		instance = new HelperReader(samFileReader);
	}

	
	/**
	 * Blocking call: must call init method first
	 */
	public static HelperReader getInstance() {
		while (instance == null) {
			logger.debug("Waiting on HelperReader instance");
			Timer.sleep(500);
		}
		return instance;
	}
	
		
	public List<SAMSequenceRecord> getReferences() {
		return references;
	}
	
	
	public boolean hasIndex() {
		return hasIndex;
	}


	public void close() {
		reader.close();
	}
		
}
