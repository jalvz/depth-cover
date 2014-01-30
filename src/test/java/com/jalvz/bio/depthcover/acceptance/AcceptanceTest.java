package com.jalvz.bio.depthcover.acceptance;

import static com.jalvz.bio.depthcover.reader.SampleFileReader.getOutput;
import static com.jalvz.bio.depthcover.reader.SampleFileReader.recipient;
import static com.jalvz.bio.depthcover.reader.SampleFileReader.sampleFile;
import static com.jalvz.bio.depthcover.reader.SampleFileReader.simpleBed;
import static com.jalvz.bio.depthcover.reader.SampleFileReader.fastaFile;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Scanner;

import org.junit.Before;
import org.junit.Test;

import com.google.common.base.Charsets;
import com.google.common.collect.Lists;
import com.google.common.io.Files;
import com.jalvz.bio.depthcover.EndPoint;
import com.jalvz.bio.depthcover.exec.ConcurrentExecutor;
import com.jalvz.bio.depthcover.exec.ExecutorStrategy;
import com.jalvz.bio.depthcover.input.UserConfigurationFixture;
import com.jalvz.bio.depthcover.model.hdata.LazyHeaderDataFixture;
import com.jalvz.bio.depthcover.writer.StatefulResultWriter;

public class AcceptanceTest {

	private StatefulResultWriter r1Writer, r2Writer;
	
	private ExecutorStrategy mockFactory;
	
	private final int MAX = Integer.MAX_VALUE;
	
	// forces header calculation
	{
		LazyHeaderDataFixture.getHeader();
	}
	
	@Before
	public void setup() throws IOException {

		new File(recipient(), "sorted.summary.csv").delete();
		new File(recipient(), "sorted.coverage.csv").delete();
		new File(recipient(), "sorted.breakdown.csv").delete();
		new File(recipient(), "sorted.details.csv").delete(); 
		new File(recipient(), "newname.coverage.csv").delete();
		new File(recipient(), "newname.breakdown.csv").delete();
		new File(recipient(), "newname.summary.csv").delete();

		
		mockFactory = new ExecutorStrategy(sampleFile(), recipient()) {
			@Override
			public StatefulResultWriter getLazyResultWriter(String str, boolean printDetails, long limit) {
				return r1Writer;
			}
		};

		r1Writer = new StatefulResultWriter(null, "out", false, MAX) {
			@Override
			public void flush() {
				TestState.getInstance().r1 = this.toString();
			}
			@Override
			public void finalise() {
				flush();
			}
		};
		
		r2Writer = new StatefulResultWriter(null, "out", false, MAX) {
			@Override
			public void flush() {
				TestState.getInstance().r2 = this.toString();
			}
			@Override
			public void finalise() {
				flush();
			}
		};
		
		
		TestState.getInstance().r1 = null;
		TestState.getInstance().r2 = null;
		try {
			new File(getOutput()).delete();
		} catch (Exception e) {}
	}
	
	@Test(timeout = 10000)
	public void testResults() {
		ConcurrentExecutor executor = new ConcurrentExecutor(mockFactory);
		executor.execute(UserConfigurationFixture.getLowFlagNoDetails());
		assertSummaryResults(TestState.getInstance().r1);
		assertBreakdownResults(TestState.getInstance().r1);
	}
	

	
	@Test(timeout = 10000)
	public void testAccumulator() {
		ConcurrentExecutor executor = new ConcurrentExecutor(mockFactory);
		executor.execute(UserConfigurationFixture.getLowFlagNoDetails());
		int totalAtDepth8 = 0;
		String[] lines = TestState.getInstance().r1.split("\n");
		for (String line : lines) {
			String[] fields = line.split("\t");
			if (fields.length > 2 && fields[2].equals("8") && !fields[1].equals("ALL")) {
				totalAtDepth8 += Integer.parseInt(fields[3]);
			}
		}
		assertTrue(TestState.getInstance().r1.contains("14-5_S5_L001_R1	8	" + totalAtDepth8));
	}

	
	@Test(timeout = 10000)
	public void testParallelResults() {
	
		ConcurrentExecutor simple = new ConcurrentExecutor(mockFactory);
		
		simple.execute(UserConfigurationFixture.getLowFlagNoDetails());
		
		ConcurrentExecutor multi = new ConcurrentExecutor(new ExecutorStrategy(sampleFile(), recipient()) {
			@Override
			public StatefulResultWriter getLazyResultWriter(String str, boolean printDetails, long limit) {
				return r2Writer;
			}
			@Override
			protected boolean parallelizable(boolean forceSequentialRead) {
				return true;
			};
		});
		
		multi.execute(UserConfigurationFixture.getNoDetails());
				assertTrue(TestState.getInstance().r1.equals(TestState.getInstance().r2));
	}


	@Test(timeout = 10000)
	public void testWriterAndMain() throws IOException {
		File outDir = recipient();
		
		EndPoint.main(new String[]{"-bam", sampleFile().getAbsolutePath(), "-d", outDir.getAbsolutePath()});

		
		File sum = new File(outDir.getAbsolutePath(), "sorted.summary.csv"); 
		String sumDiskContent = new String(Files.toString(sum, Charsets.UTF_8));
		assertMeanResults(sumDiskContent);
		
		File cov = new File(outDir.getAbsolutePath(), "sorted.coverage.csv"); 
		String covDiskContent = new String(Files.toString(cov, Charsets.UTF_8));
		assertSummaryResults(covDiskContent);
		
		File brkdwn = new File(outDir.getAbsolutePath(), "sorted.breakdown.csv");
		String brkdwnDiskContent = new String(Files.toString(brkdwn, Charsets.UTF_8));
		assertBreakdownResults(brkdwnDiskContent);
	}

	
	@Test(timeout = 10000)
	public void testWriterAndMainSideFeatures() throws IOException {
		
		File outDir = recipient();
		
		EndPoint.main(new String[]{"-bam", sampleFile().getAbsolutePath(), "-d", outDir.getAbsolutePath(), 
				"-bed", simpleBed().getAbsolutePath(), "-fasta", fastaFile().getAbsolutePath(), "-n", "newname", "-l", "3"});

		File sum = new File(outDir.getAbsolutePath(), "newname.summary.csv"); 
		String sumDiskContent = new String(Files.toString(sum, Charsets.UTF_8));
		System.out.println(sumDiskContent);
		assertTrue(sumDiskContent.contains("14-5_S5_L001_R1	chr3 105420-105429 (match-1-ini)	1	1	unknown	unknown"));
		assertTrue(sumDiskContent.contains("14-5_S5_L001_R1	chr3 105477-105482 (match-2-end)	1	2	unknown	unknown"));
		
		File cov = new File(outDir.getAbsolutePath(), "newname.coverage.csv"); 
	
		String covDiskContent = new String(Files.toString(cov, Charsets.UTF_8));
	
		File brkdwn = new File(outDir.getAbsolutePath(), "newname.breakdown.csv");
		String brkdwnDiskContent = new String(Files.toString(brkdwn, Charsets.UTF_8));

		assertFastaAndBed(covDiskContent + brkdwnDiskContent);
	}
	
	
	
	@Test(timeout = 10000)
	public void testBedNoIndex() throws IOException {
		
		File outDir = recipient();
		
		EndPoint.main(new String[]{"-bam", sampleFile().getAbsolutePath(), "-d", outDir.getAbsolutePath(), 
				"-bed", simpleBed().getAbsolutePath(), "--ignore-index", "-n", "newname"});

		File sum = new File(outDir.getAbsolutePath(), "newname.summary.csv"); 
		String sumDiskContent = new String(Files.toString(sum, Charsets.UTF_8));
		System.out.println(sumDiskContent);
		assertTrue(sumDiskContent.contains("14-5_S5_L001_R1	chr3 105420-105429 (match-1-ini)	1	1	198022430	0.0"));
		assertTrue(sumDiskContent.contains("14-5_S5_L001_R1	chr3 105477-105482 (match-2-end)	1	2	198022430	0.0"));
		
		File cov = new File(outDir.getAbsolutePath(), "newname.coverage.csv"); 
	
		String covDiskContent = new String(Files.toString(cov, Charsets.UTF_8));
		System.out.println(covDiskContent);
	
		File brkdwn = new File(outDir.getAbsolutePath(), "newname.breakdown.csv");
		String brkdwnDiskContent = new String(Files.toString(brkdwn, Charsets.UTF_8));

		assertBed(covDiskContent + brkdwnDiskContent);
	}
	
	
	
	@Test
	public void testHelp() throws IOException {
		
		File outDir = recipient();
		File outFile = new File(outDir.getAbsolutePath(), "sorted.coverage.csv");
		
		EndPoint.main(new String[]{sampleFile().getAbsolutePath(), "-d", outDir.getAbsolutePath(), "--help"});
		assertFalse(outFile.exists());
	}
	
	
	@Test(timeout = 10000)
	public void testLinesOrder() throws FileNotFoundException {
		
		ConcurrentExecutor multi = new ConcurrentExecutor(new ExecutorStrategy(sampleFile(), recipient()) {
			@Override
			protected boolean parallelizable(boolean forceSequentialRead) {
				return true;
			};
		});
		
		multi.execute(UserConfigurationFixture.getNoDetails());
		
		
		List<String> lines = Lists.newArrayList();
		Scanner sc = new Scanner(new File(recipient().getAbsolutePath(), "sorted.breakdown.csv"));
		while (sc.hasNextLine()) {
			lines.add(sc.nextLine());
		}
		assertTrue(lines.get(1).contains("chr1"));
		assertTrue(lines.get(21).contains("chr2"));
		assertTrue(lines.get(24).contains("chr3"));
		assertTrue(lines.get(45).contains("chr7"));
		assertTrue(lines.get(47).contains("chr8"));
		assertTrue(lines.get(59).contains("chr12"));
		assertTrue(lines.get(60).contains("chr13"));
		assertTrue(lines.get(70).contains("chr17"));
		assertTrue(lines.get(71).contains("chr18"));
		assertTrue(lines.get(90).contains("chrX"));
		assertTrue(lines.get(94).contains("chrY"));
		assertTrue(lines.get(95).contains("chrM"));
		sc.close();
	}
	
	
	@Test
	public void testMainAndPrintDetails() throws IOException {
		File outDir = recipient();
		
		EndPoint.main(new String[]{"-bam", sampleFile().getAbsolutePath(), "-d", outDir.getAbsolutePath(), "-a", "-bed", simpleBed().getAbsolutePath()});

		File sum = new File(outDir.getAbsolutePath(), "sorted.details.csv"); 
		String detailsContent = new String(Files.toString(sum, Charsets.UTF_8));

		assertTrue(detailsContent.contains("chr3 105420-105429 (match-1-ini)	105420	0"));
		assertTrue(detailsContent.contains("chr3 105420-105429 (match-1-ini)	105429	1"));
		assertTrue(detailsContent.contains("chr2 20160-20265 (match-48)	20160	0"));
		assertTrue(detailsContent.contains("chr2 20160-20265 (match-48)	20180	1"));
		assertTrue(detailsContent.contains("chr2 20160-20265 (match-48)	20213	0"));
		assertTrue(detailsContent.contains("chrM 1-16569	15418	1"));
		assertTrue(detailsContent.contains("chrM 1-16569	15419	2"));
		assertTrue(detailsContent.contains("chrM 1-16569	15420	3"));
	}
	
	
	
	private void assertMeanResults(String result) {
		assertTrue(result.contains("14-5_S5_L001_R1	ALL	187704	8817970	3095693981	0.002848"));
		assertTrue(result.contains("14-5_S5_L001_R1	chr1	15075	706825	249250621	0.002836"));
		assertTrue(result.contains("14-5_S5_L001_R1	chr2	16287	765881	243199373	0.003149"));
		assertTrue(result.contains("14-5_S5_L001_R1	chrM	80	3784	16569	0.228378"));
	}
	
	
	private void assertSummaryResults(String result) {
		assertTrue(result.contains("14-5_S5_L001_R1	1	8791842	3095693981	0.00284"));
		assertTrue(result.contains("14-5_S5_L001_R1	2	20061	3095693981"));
		assertTrue(result.contains("14-5_S5_L001_R1	19	12"));
	}

	private void assertBreakdownResults(String result) {
		assertTrue(result.contains("14-5_S5_L001_R1	chr20	2	472"));
		assertTrue(result.contains("14-5_S5_L001_R1	chr6	9	6"));
		assertTrue(result.contains("14-5_S5_L001_R1	chr8	1	467534"));
		assertTrue(result.contains("14-5_S5_L001_R1	chr1	2	1741"));
		assertTrue(result.contains("14-5_S5_L001_R1	chrX	4	31"));
		assertTrue(result.contains("14-5_S5_L001_R1	chrY	2	85"));
		assertTrue(result.contains("14-5_S5_L001_R1	chrM	1	2751	16569	0.166"));
		assertTrue(result.contains("14-5_S5_L001_R1	chrM	2	799	16569"));
		assertTrue(result.contains("14-5_S5_L001_R1	chrM	3	234	16569"));
	}

	
	private void assertFastaAndBed(String result) {
		assertTrue(result.contains("14-5_S5_L001_R1	1	2842	47	60.468085"));
		assertTrue(result.contains("14-5_S5_L001_R1	2	799	47	17.0"));
		assertTrue(result.contains("14-5_S5_L001_R1	chr3 105477-105482 (match-2-end)	1	2	unknown	unknown"));
		assertTrue(result.contains("14-5_S5_L001_R1	chrM 1-16569	1	2751	unknown	unknown"));
		assertTrue(result.contains("14-5_S5_L001_R1	chr2 20160-20265 (match-48)	1	48	47	1.021277"));
		assertFalse(result.contains("14-5_S5_L001_R1	chrM 1-16569	3	234	unknown	unknown"));
	}
	
	
	private void assertBed(String result) {
		assertTrue(result.contains("14-5_S5_L001_R1	1	2842	441238372	6.0E-6"));
		assertTrue(result.contains("14-5_S5_L001_R1	2	799	441238372	2.0E-6"));
		assertTrue(result.contains("14-5_S5_L001_R1	chr3 105477-105482 (match-2-end)	1	2	198022430	0.0"));
		assertTrue(result.contains("14-5_S5_L001_R1	chrM 1-16569	1	2751	16569	0.166033"));
		assertTrue(result.contains("14-5_S5_L001_R1	chr2 20160-20265 (match-48)	1	48	243199373	0.0"));
		assertTrue(result.contains("14-5_S5_L001_R1	chrM 1-16569	3	234	16569	0.014123"));
	}

}
