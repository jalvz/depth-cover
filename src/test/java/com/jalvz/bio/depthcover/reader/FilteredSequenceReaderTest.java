package com.jalvz.bio.depthcover.reader;

import static com.jalvz.bio.depthcover.reader.SampleFileReader.sampleFile;
import static com.jalvz.bio.depthcover.reader.SampleFileReader.simpleBed;

import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.Maps;
import com.jalvz.bio.depthcover.analyser.calc.StandardCoverageCalculator;
import com.jalvz.bio.depthcover.ds.queue.CloseableQueue;
import com.jalvz.bio.depthcover.model.cdata.CoverageData;
import com.jalvz.bio.depthcover.model.hdata.LazyHeaderDataFixture;
import com.jalvz.bio.depthcover.model.idata.IntervalData;
import com.jalvz.bio.depthcover.model.idata.lookup.IntervalLookupDataSet;
import com.jalvz.bio.depthcover.reader.FilteredSequenceReader;
import com.jalvz.bio.depthcover.reader.SequenceReader;
import com.jalvz.bio.depthcover.reader.opt.IntervalLookupDataReader;

public class FilteredSequenceReaderTest {
	
	private SequenceReader seqReader;
	
	private CloseableQueue queue;
	
	private IntervalLookupDataSet intervalLookupDataSet;
	
	private StandardCoverageCalculator calc;
	
	// forces header calculation
	{
		LazyHeaderDataFixture.getHeader().getTotal();
	}
	
	@Before
	public void setup() {
		queue = new CloseableQueue(50);
		calc = new StandardCoverageCalculator();
		IntervalLookupDataReader bedReader = new IntervalLookupDataReader(simpleBed());
		new Thread(bedReader).start();
		intervalLookupDataSet = bedReader.get();
		seqReader = new FilteredSequenceReader(queue,  sampleFile(), intervalLookupDataSet);
	}
	
	@Test
	public void test() throws InterruptedException {
		Map<String, Map<Integer, Long>> rsmap = Maps.newHashMap();
		Thread t = new Thread(seqReader);
		t.start();
		t.join();
		while (!queue.isEmpty()) {
			IntervalData idata = queue.take();
			idata.prepare();
			CoverageData cdata = calc.calculate(idata);
			rsmap.put(cdata.getReferenceName(), cdata.getCoverage());
		}
		IntervalAssertions.checkResults(rsmap);
	}

}
