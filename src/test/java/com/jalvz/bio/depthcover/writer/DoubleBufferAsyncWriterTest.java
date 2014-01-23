package com.jalvz.bio.depthcover.writer;

import static com.jalvz.bio.depthcover.reader.SampleFileReader.recipient;
import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.Random;

import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.Lists;
import com.jalvz.bio.depthcover.util.Timer;

public class DoubleBufferAsyncWriterTest {

	File out;
	
	@Before
	public void setup() throws IOException {
		out = new File(recipient(), "test2.csv");
		out.delete();
		out.createNewFile();
	}
	
	@Test
	public void testWrite() throws IOException {
		DoubleBufferAsyncWriter writer = DoubleBufferAsyncWriterFixture.initAndGetAsyncWriter(out);
		Random rand = new Random();
		List<String> ln;
		int idx = 0;
		for (idx = 0; idx < 1000000; idx++) {
			if (idx % 10000 == 0) {
				Timer.sleep(30);
			}
			ln = Lists.newArrayList(String.valueOf(rand.nextLong()), "QWERTYUIOPASDFGHJKLZXCVBNM1234567890", "IDX-"+idx);
			writer.addLine(ln);
		}
		writer.finalise();
		
		BufferedReader reader = new BufferedReader(new FileReader(out));
		String line = reader.readLine();
		idx = 0;
		while ((line = reader.readLine()) != null) {
			assertTrue(line.contains("IDX-"+idx));
			idx+=1;
		}
		reader.close();
	}
	

}
