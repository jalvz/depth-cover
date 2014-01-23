package com.jalvz.bio.depthcover.reader.opt;

import static com.jalvz.bio.depthcover.reader.SampleFileReader.bedFile;
import static org.junit.Assert.*;

import java.util.List;

import org.junit.Test;

import com.jalvz.bio.depthcover.model.idata.IntervalData;
import com.jalvz.bio.depthcover.model.idata.lookup.IntervalLookupDataSet;
import com.jalvz.bio.depthcover.reader.opt.IntervalLookupDataReader;

public class IntervalLookupDataReaderTest {

	@Test(timeout = 3000)
	public void testResults() {
		IntervalLookupDataReader bedReader = new IntervalLookupDataReader(bedFile());
		new Thread(bedReader).start();
		IntervalLookupDataSet result = bedReader.get();

		List<IntervalData> builders = result.intersectionsSE("sample1", "chr7", new int[]{127472300, 127475865});
		assertTrue(builders.size() == 5);
		assertTrue(builders.get(0).getReferenceName().equals("chr7 127471196-127472363 (Pos1)"));
		assertTrue(builders.get(0).getOpenings().get(0) == 127472300);
		assertTrue(builders.get(0).getClosings().get(0) == 127472364);
		assertTrue(result.intersectionsSE("sample1", "chr7", new int[]{127477039, 127477054}).size() == 1);
		assertFalse(result.containsSL("chr1", new int[]{127472300, 3565}));
		assertFalse(result.containsSL("chr7", new int[]{127471000, 195}));
		assertFalse(result.containsSL("chr7", new int[]{127481700, 2}));
	}

}
