package com.jalvz.bio.depthcover.reader;

import static com.jalvz.bio.depthcover.reader.SampleFileReader.sampleFile;
import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import com.jalvz.bio.depthcover.ds.queue.CloseableQueue;
import com.jalvz.bio.depthcover.reader.ParallelSequenceReader;
import com.jalvz.bio.depthcover.reader.SequenceReader;

public class ParallelSequenceReaderTest {

	private SequenceReader seqReader;
	
	private CloseableQueue queue;
	
	@Before
	public void setup() {
		queue = new CloseableQueue(50);
		seqReader = new ParallelSequenceReader(queue, sampleFile());
	}
	
	@Test
	public void test() throws InterruptedException {
		new Thread(seqReader).start();
		Thread.sleep(5000);
		assertTrue(queue.size() == 25);
	}

}
