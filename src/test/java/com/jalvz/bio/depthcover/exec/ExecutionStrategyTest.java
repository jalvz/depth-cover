package com.jalvz.bio.depthcover.exec;

import static com.jalvz.bio.depthcover.reader.SampleFileReader.bedFile;
import static com.jalvz.bio.depthcover.reader.SampleFileReader.fastaFile;
import static com.jalvz.bio.depthcover.reader.SampleFileReader.notIndexedFile;
import static com.jalvz.bio.depthcover.reader.SampleFileReader.notOrderedFile;
import static com.jalvz.bio.depthcover.reader.SampleFileReader.recipient;
import static com.jalvz.bio.depthcover.reader.SampleFileReader.sampleFile;
import static org.junit.Assert.assertTrue;

import java.io.File;

import org.junit.Before;
import org.junit.Test;

import com.jalvz.bio.depthcover.analyser.DepthOfCoverageAnalyser;
import com.jalvz.bio.depthcover.analyser.calc.StandardCoverageCalculator;
import com.jalvz.bio.depthcover.analyser.calc.StepCoverageCalculator;
import com.jalvz.bio.depthcover.model.idata.lookup.IntervalLookupDataSet;
import com.jalvz.bio.depthcover.reader.ContinuousSequenceReader;
import com.jalvz.bio.depthcover.reader.FilteredSequenceReader;
import com.jalvz.bio.depthcover.reader.ParallelSequenceReader;
import com.jalvz.bio.depthcover.reader.SequenceReader;
import com.jalvz.bio.depthcover.reader.SkipableSequenceReader;
import com.jalvz.bio.depthcover.reader.opt.HeadSequenceReader;
import com.jalvz.bio.depthcover.reader.opt.IntervalLookupDataReader;
import com.jalvz.bio.depthcover.reader.opt.ReferenceSequenceReader;

public class ExecutionStrategyTest {

	private File samFile = sampleFile();
	
	private ExecutorStrategy strategy;
	
	@Before
	public void setup() {
		strategy = new ExecutorStrategy(samFile, recipient());
		// instantiates writer
		strategy.getLazyResultWriter("", false, Integer.MAX_VALUE);
	}
	
	@Test
	public void testSingle() {
		SequenceReader reader = strategy.getSequenceReader(false);
		assertTrue(reader instanceof ParallelSequenceReader);
		
		reader = strategy.getSequenceReader(true);
		assertTrue(reader instanceof ContinuousSequenceReader);
	}

	
	
	@Test
	public void testParallel() {
		SequenceReader reader = new ExecutorStrategy(samFile, recipient()) {
			@Override
			protected boolean parallelizable(boolean forceSequentialRead) {
				return true;
			};
		}.getSequenceReader(false);
		assertTrue(reader instanceof ParallelSequenceReader);
	}

	
	@Test
	public void testNotIndex() {
		SequenceReader reader = new ExecutorStrategy(notIndexedFile(), recipient()) {
			@Override
			protected boolean parallelizable(boolean forceSequentialRead) {
				return false;
			};
		}.getSequenceReader(false);
		assertTrue(reader instanceof ContinuousSequenceReader);
	}

	
	@Test(expected = IllegalArgumentException.class)
	public void testNotOrdered() {
		new ExecutorStrategy(notOrderedFile(), recipient());
	}
	
	
	@Test
	public void testAnalyser() {
		DepthOfCoverageAnalyser analyser1 = strategy.getSequenceAnalyser(
				strategy.getLazyResultWriter("", false, Integer.MAX_VALUE));
		assertTrue(analyser1.getCalculator() instanceof StandardCoverageCalculator);

		DepthOfCoverageAnalyser analyser2 = strategy.getSequenceAnalyser(
				strategy.getLazyResultWriter("", true, Integer.MAX_VALUE));
		
		assertTrue(analyser2.getCalculator() instanceof StepCoverageCalculator);
	}
	
	
	@Test
	public void testPartialReader() {
		SequenceReader reader1 = strategy.getPartialSequenceReader(new IntervalLookupDataSet(), false);
		assertTrue(reader1 instanceof FilteredSequenceReader);
		
		SequenceReader reader2 = new ExecutorStrategy(notIndexedFile(), recipient()).getPartialSequenceReader(
				new IntervalLookupDataSet(), true);
		assertTrue(reader2 instanceof SkipableSequenceReader);
	}
	
	
	@Test
	public void testIntervalLookupReader() {
		assertTrue(new ExecutorStrategy(samFile, recipient()).getIntervalLookupReader(bedFile()) instanceof IntervalLookupDataReader);
	}
	
	
	@Test
	public void testHeaderReader() {
		assertTrue(new ExecutorStrategy(samFile, recipient()).getHeadSequenceReader(fastaFile()) instanceof ReferenceSequenceReader);
		assertTrue(new ExecutorStrategy(samFile, recipient()).getHeadSequenceReader(null) instanceof HeadSequenceReader);

	}
}
