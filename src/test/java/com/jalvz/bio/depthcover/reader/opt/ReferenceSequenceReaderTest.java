package com.jalvz.bio.depthcover.reader.opt;

import static com.jalvz.bio.depthcover.reader.SampleFileReader.fastaFile;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.Map;

import org.junit.Test;

import com.jalvz.bio.depthcover.model.hdata.LazyHeaderDataFixture;

public class ReferenceSequenceReaderTest {

	// triggers header calculation
	{
		LazyHeaderDataFixture.getHeader().getTotal();
	}

	
	@Test(timeout = 3000)
	public void testResults() {

		ReferenceSequenceReader fastaReader = new ReferenceSequenceReader(fastaFile());
		new Thread(fastaReader).start();
		Map<String, Long> result = fastaReader.get();
		
		assertTrue(result.size() == 3);
		assertTrue(result.get("chr1") == 82);
		assertTrue(result.get("chr2") == 47);
		assertTrue(result.get("ALL") == 82 + 47);
	}
	
	
	@Test
	public void testIOError() {
		ReferenceSequenceReader fastaReader = new ReferenceSequenceReader(new File("/", "*/a"));
		new Thread(fastaReader).start();
		Map<String, Long> result = fastaReader.get();
		assertTrue(result.size() == 0);
	}

	
}
