package com.jalvz.bio.depthcover.model.cdata;

import static com.jalvz.bio.depthcover.reader.SampleFileReader.recipient;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

import com.jalvz.bio.depthcover.model.hdata.LazyHeaderDataFixture;
import com.jalvz.bio.depthcover.writer.StatefulResultWriter;

public class CoverageDataResultTest {

	CoverageDataResult coverageDataResult;
	String result;
	
	// forces header calculation
	{
		LazyHeaderDataFixture.getHeader();
	}
	
	@Before
	public void setup() {
		coverageDataResult = new CoverageDataResult();
	}
	
	
	@Test
	public void testSet() throws IOException {
		assertTrue(coverageDataResult.isEmpty());
		
		CoverageData cdata1 = new CoverageData("sample1", "chr1", "chr1");
		cdata1.getCoverage().put(1, 10L);
		cdata1.getCoverage().put(2, 20L);
		cdata1.getCoverage().put(3, 40L);

		CoverageData cdata2 = new CoverageData("sample1", "chr2", "chr2");
		cdata2.getCoverage().put(2, 30L);
		cdata2.getCoverage().put(3, 100L);

		CoverageData cdata3 = new CoverageData("sample9", "chrX", "chrX");
		cdata3.getCoverage().put(2, 50L);
		cdata3.getCoverage().put(4, 1000L);

		StatefulResultWriter writer = new StatefulResultWriter(recipient(), "out", false, Integer.MAX_VALUE) {
			@Override
			public void flush() {
				result = this.toString();
			}
			@Override
			public void finalise() {
				flush();
			}
		};
		coverageDataResult.set(cdata1);
		coverageDataResult.set(cdata2);
		coverageDataResult.set(cdata3);
		coverageDataResult.populateWriter(writer);
		
		assertTrue(result.contains("sample1	chr1	1	70"));
		assertTrue(result.contains("sample1	chr2	2	130"));
		assertTrue(result.contains("sample1	chr2	3	100"));
		assertTrue(result.contains("sample1 sample9	2	1240"));
		assertTrue(result.contains("sample1 sample9	1	1250"));
		assertTrue(result.contains("sample9	chrX	4	1000"));
	}

}
