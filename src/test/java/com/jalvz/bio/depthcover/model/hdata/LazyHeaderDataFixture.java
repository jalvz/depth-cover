package com.jalvz.bio.depthcover.model.hdata;

import java.util.List;
import java.util.Map;

import net.sf.samtools.SAMSequenceRecord;
import static com.jalvz.bio.depthcover.reader.SampleFileReader.sampleFile;

import com.jalvz.bio.depthcover.reader.HelperReader;
import com.jalvz.bio.depthcover.reader.async.SafeAsyncReader;
import com.jalvz.bio.depthcover.reader.opt.HeadSequenceReader;

public class LazyHeaderDataFixture {

	public static LazyHeaderData getHeader() {
		HelperReader.init(sampleFile());
		SafeAsyncReader<Map<String, Long>> reader = new HeadSequenceReader();
		Thread t = new Thread(reader);
		t.start();
		try {
			t.join();
		} catch (InterruptedException e) {
		}
		LazyHeaderData.init(reader);
		return LazyHeaderData.getInstance();
	}
	
	
	public static List<SAMSequenceRecord> getDictionary() {
		HelperReader.init(sampleFile());
		return HelperReader.getInstance().getReferences();
	}
}
