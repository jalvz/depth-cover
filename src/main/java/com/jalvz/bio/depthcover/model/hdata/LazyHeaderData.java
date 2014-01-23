package com.jalvz.bio.depthcover.model.hdata;

import java.util.List;
import java.util.Map;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.jalvz.bio.depthcover.Tags;
import com.jalvz.bio.depthcover.reader.async.SafeAsyncReader;

/**
 * Global state for the application. 
 * It is supposed to be immutable once is calculated.
 * Holds a map with reference names and lengths for the BAM file.
 * It is used by the consumers and the producer.
 * Needs to be initialised - get() calls blocks until data is available.
 */
public class LazyHeaderData {
	
	
	private static LazyHeaderData instance;
	
	private final Map<String, Long> header;

	private final SafeAsyncReader<Map<String, Long>> headerFuture;
	
	private boolean computed = false;
	
	private LazyHeaderData(SafeAsyncReader<Map<String, Long>> future) {
		this.headerFuture = future;
		this.header = Maps.newHashMap();
	}
	
	/*
	 * User of this class is responsible to call init only once
	 */
	public static void init(SafeAsyncReader<Map<String, Long>> future) {
		instance = new LazyHeaderData(future);
	}
	
	
	public static LazyHeaderData getInstance() {
		return instance;
	}
	
	
	public static List<String> getGlobalHeadline() {
		 return Lists.newArrayList(Tags.SAMPLE, Tags.DEPTH, Tags.ALIGNED, Tags.TOTAL, Tags.PROPORTION);
	}

	public static List<String> getAveragesHeadline() {
		 return Lists.newArrayList(Tags.SAMPLE, Tags.REFERENCE, Tags.READS, Tags.ALIGNED, Tags.TOTAL, Tags.COVERAGE_MEAN);
	}

	
	public static List<String> getReferenceHeadline() {
		 return Lists.newArrayList(Tags.SAMPLE, Tags.REFERENCE, Tags.DEPTH, Tags.ALIGNED , Tags.TOTAL, Tags.PROPORTION);
	}
	
	
	public static List<String> getDetailsHeadline() {
		 return Lists.newArrayList(Tags.REFERENCE, Tags.POSITION, Tags.DEPTH);
	}
	

	public boolean isEmpty() {
		return header.isEmpty();
	}
	

	/**
	 * Forces evaluation
	 */
	public Long get(String ref) {
		sync();
		if (header.containsKey(ref)) {
			return header.get(ref);
		}
		return -1L;
	}

	
	/**
	 * Forces evaluation
	 */
	public Long getTotal() {
		return get(Tags.ALL);
	}
	
	
	/**
	 * Forces evaluation
	 */
	public void overrideTotal(long total) {
		sync();
		header.put(Tags.ALL, total);
	}
	
	
	private void sync() {
		if (!computed) {
			header.putAll(headerFuture.get());
			computed = true;
		}
	}
	
}
