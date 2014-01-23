package com.jalvz.bio.depthcover.analyser.calc;

import static com.jalvz.bio.depthcover.reader.SampleFileReader.recipient;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.google.common.base.Charsets;
import com.google.common.io.Files;
import com.jalvz.bio.depthcover.model.idata.IntervalDataFixture;
import com.jalvz.bio.depthcover.writer.DoubleBufferAsyncWriter;
import com.jalvz.bio.depthcover.writer.DoubleBufferAsyncWriterFixture;

public class StepCoverageCalculatorTest {

	private File out;

	private StepCoverageCalculator  calculator;

	private DoubleBufferAsyncWriter writer;
	
	@Before
	public void setup() throws IOException {
		out = new File(recipient(), "test_step.csv");
		out.createNewFile();
		writer = DoubleBufferAsyncWriterFixture.initAndGetAsyncWriter(out);
		calculator = new StepCoverageCalculator(writer);
	}
	
	@Test
	public void testSteper() throws IOException {
		calculator.calculate(IntervalDataFixture.custom(2,3,3,7,6,2,12,4));
		writer.finalise();
		String steps = new String(Files.toString(out, Charsets.UTF_8));
		assertTrue(steps.contains("chr1\t1\t0"));
		assertTrue(steps.contains("chr1\t2\t1"));
		assertTrue(steps.contains("chr1\t4\t2"));
		assertTrue(steps.contains("chr1\t5\t1"));
		assertTrue(steps.contains("chr1\t7\t2"));
		assertTrue(steps.contains("chr1\t10\t0"));
		assertTrue(steps.contains("chr1\t24\t0"));
		assertFalse(steps.contains("chr1\t26"));
	}
	
	@After
	public void teardown() {
		out.delete();
	}
}
