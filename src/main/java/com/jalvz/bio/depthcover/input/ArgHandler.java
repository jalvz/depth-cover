package com.jalvz.bio.depthcover.input;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.log4j.Logger;



public class ArgHandler {

	private static final Logger logger = Logger.getLogger(ArgHandler.class.getName());
	
	
	public static File getIn(List<String> args) {
		return getFile(args, "-bam", "BAM file");
	}

	
	public static File getFasta(List<String> args) {
		return getFile(args, "-fasta", "FASTA file");
	}


	public static File getBed(List<String> args) {
		return getFile(args, "-bed", "BED file");
	}

	
	public static long getDepthLimit(List<String> args) {
		if (args.contains("-l")) {
			try {
				return Integer.parseInt(args.get(args.indexOf("-l") + 1));
			} catch (Exception e) {
				logger.trace(e.getMessage(), e);
				throw new IllegalArgumentException("Please specify a valid limit after -l");
			}
		}
		return Long.MAX_VALUE;
	}

	
	public static String getPreffix(List<String> args) {
		if (args.contains("-n")) {
			try {
				return args.get(args.indexOf("-n") + 1);
			} catch (Exception e) {
				logger.trace(e.getMessage(), e);
				throw new IllegalArgumentException("Please specify a valid name after -n");
			}
		}
		return getIn(args).getName().replaceAll(".bam", "");
	}

	
	public static boolean getPrintDetailsFlag(List<String> args) {
		return args.contains("-a") || args.contains("--all");
	}

	
	public static File getOutDir(List<String> args, File in) {
		File out = getFile(args, "-d", "directory");
		if (out != null) {
			return out;
		}
		return new File(in.getParentFile().getAbsolutePath());
	}
	
	
	public static boolean getLowFlag(List<String> args) {
		return args.contains("--ignore-index");
	}

	
	public static boolean getHelp(List<String> args) {
		return args.contains("-h") || args.contains("--help");
	}

		
	
	private static File getFile(List<String> args, String option, String msg) {
		if (args.contains(option)) {
			try {
				int outIdx = args.indexOf(option) + 1;
				return new File(args.get(outIdx));
			} catch (Exception e) {
				logger.trace(e.getMessage(), e);
				throw new IllegalArgumentException("Please specify a valid " + msg + " after " + option);
			}
		}
		return null;
	}


}
