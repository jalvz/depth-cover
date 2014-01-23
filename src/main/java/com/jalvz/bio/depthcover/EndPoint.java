package com.jalvz.bio.depthcover;


import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.jalvz.bio.depthcover.exec.ConcurrentExecutor;
import com.jalvz.bio.depthcover.exec.ExecutorStrategy;
import com.jalvz.bio.depthcover.input.ArgHandler;
import com.jalvz.bio.depthcover.input.ArgValidator;
import com.jalvz.bio.depthcover.input.UserConfiguration;
import com.jalvz.bio.depthcover.util.Timer;


public class EndPoint {

	
	private static final Logger logger = Logger.getLogger(EndPoint.class.getName());

	public static void main(String[] args) throws IOException {
			
		Timer timer = new Timer();

		List<String> argList = Lists.newArrayList(args);
		
		if (args.length == 0 || ArgHandler.getHelp(argList)) {
		
			printHelp();
		
		} else {
		
			File in = ArgHandler.getIn(argList);
			File out = ArgHandler.getOutDir(argList, in);
			boolean lowFlag = ArgHandler.getLowFlag(argList) || ArgValidator.forceSequentialRead(in);
			boolean printDetails = ArgHandler.getPrintDetailsFlag(argList);
			long depthLimit = ArgHandler.getDepthLimit(argList);
			String outName = ArgHandler.getPreffix(argList);
			File bed = ArgHandler.getBed(argList);
			File fasta = ArgHandler.getFasta(argList);
			
			try {
				ArgValidator.smoke(in, outName, out, Joiner.on(" ").join(argList));
				logger.info("Started.");
				UserConfiguration config = new UserConfiguration(lowFlag, printDetails, depthLimit, outName, bed, fasta);
				new ConcurrentExecutor(new ExecutorStrategy(in, out)).execute(config);
			} catch (IllegalArgumentException e) {
				logger.debug(e.getMessage(), e);
			}
			
			if (ExitStatus.getStatus() == 0) {
				printEnd(timer, out.getCanonicalPath() + "/" + outName, printDetails);
			} else {
				logger.info("Exiting.");
			}
		}
	}
	
	
		
	private static void printHelp() {
		StringBuilder sb = new StringBuilder();
		sb.append("USAGE: java -jar depth-cover.jar -bam input.bam [OPTIONS]\n\n");
		sb.append("  *NOTE*: consider the memory settings for the JVM, ie: java -Xmx2g -jar depth-cover.jar ...\n");
		sb.append("  If during execution you see an error like 'java.lang.OutOfMemoryError' you'll need to increase the\n"); 
		sb.append("  memory allocation with the -Xmx argument. The --ignore-index option can reduce the memory footprint.\n\n");
		sb.append("OPTIONS:\n");
		sb.append("  -bed FILE              Calculates the coverage in the intervals specified in the given bed FILE (default = all aligned genome)\n");
		sb.append("  -fasta FILE            Excludes Ns from the total number of alignable positions in the reference fasta FILE\n");
		sb.append("  -l LIMIT               Excludes from the output any depth of coverage higher than LIMIT\n");
		sb.append("  -a , --all             Writes the coverage for each locus in a separated file\n");
		sb.append("  -d DIRECTORY           Writes the output files to DIRECTORY (default = same as input BAM file)\n");
		sb.append("  -n NAME                Uses NAME as the file name of the output (default = same as input BAM file)\n");			
		sb.append("  --ignore-index         Ignores BAM index if present (forces sequential read)\n");
		sb.append("  -h , --help            Prints this help\n");
		logger.info(sb.toString());
	}

	
	private static void printEnd(Timer timer, String path, boolean details) {
		StringBuilder sb = new StringBuilder();
		long execTime = timer.elapsedTime();
		String execTimeStr = String.format("%d minutes, %d seconds", 
				TimeUnit.MILLISECONDS.toMinutes(execTime),
				TimeUnit.MILLISECONDS.toSeconds(execTime) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(execTime)));
		sb.append("\n\n");
		sb.append("Exec time: " + execTimeStr + ".");
		logger.debug(sb.toString());

		StringBuilder sb2 = new StringBuilder("Please check:\n");
		sb2.append(path + Tags.SUMMARY_EXT + " (mean coverage per genome and chromosomes / intervals )\n");
		sb2.append(path + Tags.COVERAGE_EXT + " (coverage per genome / intervals (if specified))\n");
		sb2.append(path + Tags.BREAKDOWN_EXT + " (coverage per chromosome / interval)\n");
		if (details) {
			sb2.append(path + Tags.DETAILS_EXT + " (coverage per locus)\n");
		}

		logger.info(sb2.toString());
		logger.info("\nDone.");

	}
	
	
}
