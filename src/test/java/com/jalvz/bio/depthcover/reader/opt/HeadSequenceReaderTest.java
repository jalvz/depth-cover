package com.jalvz.bio.depthcover.reader.opt;

import static com.jalvz.bio.depthcover.reader.SampleFileReader.sampleFile;
import static org.junit.Assert.assertTrue;

import java.util.Map;

import org.junit.Test;

import com.jalvz.bio.depthcover.exec.ExecutorStrategy;
import com.jalvz.bio.depthcover.reader.async.SafeAsyncReader;

import static com.jalvz.bio.depthcover.reader.SampleFileReader.recipient;

public class HeadSequenceReaderTest {

	@Test(timeout = 6000)
	public void testTotalCount() {
		SafeAsyncReader<Map<String, Long>> reader = new ExecutorStrategy(sampleFile(), recipient()).getHeadSequenceReader(null);
		new Thread(reader).start();
		Map<String, Long> totals = reader.get();
		assertTrue(totals.size() == 26);
		assertTrue(totals.get("chr20") == 63025520l);
		assertTrue(totals.get("chr1") == 249250621l);
		assertTrue(totals.get("chrX") == 155270560l);
		assertTrue(totals.get("ALL") == 3095693981l);
	}

}
