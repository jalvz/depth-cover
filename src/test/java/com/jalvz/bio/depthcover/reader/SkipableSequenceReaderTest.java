package com.jalvz.bio.depthcover.reader;

import static com.jalvz.bio.depthcover.reader.SampleFileReader.simpleBed;
import static com.jalvz.bio.depthcover.reader.SampleFileReader.sampleFile;

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
import com.jalvz.bio.depthcover.reader.opt.IntervalLookupDataReader;

public class SkipableSequenceReaderTest {

	private SequenceReader seqReader;
	
	private CloseableQueue queue;
	
	private IntervalLookupDataSet intervalLookupDataSet;

	private StandardCoverageCalculator calc;
	
	{
		LazyHeaderDataFixture.getHeader();
	}
	
	@Before
	public void setup() {
		calc = new StandardCoverageCalculator();
		queue = new CloseableQueue(50);
		IntervalLookupDataReader bedReader = new IntervalLookupDataReader(simpleBed());
		new Thread(bedReader).start();
		intervalLookupDataSet = bedReader.get();
		seqReader = new SkipableSequenceReader(queue, sampleFile(), intervalLookupDataSet);
	}
	
	@Test
	public void testResults() throws InterruptedException {
		Map<String, Map<Integer, Long>> rsmap = Maps.newHashMap();
		Thread t = new Thread(seqReader);
		t.start();
		t.join();
		while (!queue.isEmpty()) {
			IntervalData idata = queue.take().build();
			CoverageData cdata = calc.calculate(idata);
			rsmap.put(idata.getReferenceName(), cdata.getCoverage());
		}
		IntervalAssertions.checkResults(rsmap);
	}

}
