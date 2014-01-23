package com.jalvz.bio.depthcover.analyser;

import static com.jalvz.bio.depthcover.reader.SampleFileReader.recipient;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

import com.jalvz.bio.depthcover.ds.queue.CloseableQueue;
import com.jalvz.bio.depthcover.model.hdata.LazyHeaderDataFixture;
import com.jalvz.bio.depthcover.model.idata.IntervalDataBuilder;
import com.jalvz.bio.depthcover.writer.StatefulResultWriter;

public class DepthOfCoverageAnalyserTest {

	private CloseableQueue queue;
	private DepthOfCoverageAnalyser analyser;
	private String result = "";

	// forces header calculation
	{
		LazyHeaderDataFixture.getHeader();
	}
	
	@Before
	public void setup() throws IOException {
		queue = new CloseableQueue(2);
		analyser = new DepthOfCoverageAnalyser(queue, new StatefulResultWriter(recipient(), "", false, Integer.MAX_VALUE) {
			@Override
			public void flush() {
				result += this.toString();
			}
			@Override
			public void finalise() {
				flush();
			}
		});
	}

	@Test(timeout = 10000)
	public void testEmpty() throws InterruptedException {
		queue.close();
		Thread t = new Thread(analyser);
		t.start();
		t.join();
		assertTrue(result.contains("SAMPLE	DEPTH	ALIGNED BASES	TOTAL BASES	PROPORTION"));
		assertFalse(result.contains("chr1"));

	}
	
	@Test(timeout = 10000)
	public void testData() throws InterruptedException {
		IntervalDataBuilder idata1 = new IntervalDataBuilder("sample1", "chr1", 0, 10).
				addIntervalSL(new int[]{2,4}).addIntervalSL(new int[]{4,6});
		IntervalDataBuilder idata2 = new IntervalDataBuilder("sample1", "chr1", 0, 10).addIntervalSL(new int[]{1,2});
		queue.put(idata1);
		queue.put(idata2);
		queue.close();
		
		Thread t = new Thread(analyser);
		t.start();
		t.join();
		assertTrue(result.contains("sample1	1	10	249250621"));
		assertTrue(result.contains("sample1	chr1	1	8	249250621"));
	}

}
