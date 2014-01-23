package com.jalvz.bio.depthcover.input;

import java.io.File;

import org.apache.log4j.Logger;

import com.jalvz.bio.depthcover.ExitStatus;
import com.jalvz.bio.depthcover.Tags;

public class ArgValidator {
	
	private static final Logger logger = Logger.getLogger(ArgValidator.class.getName());
	
	public static final double MIN_MEM_REQUIRED_FACTOR = 0.1d;
	
	public static final double MIN_PARALLEL_REQUIRED_FACTOR = 0.4d;


	public static void smoke(File input, String p, File output, String cmd) {
		
		boolean ok = false;
		
		if (!checkPermissions(output)) {
			logger.error("Can not write to " + output.getAbsolutePath() +  ": file is a directory or not write permissions."); 
		} else if (!checkOutput(output, p)) {
			logger.error("Can not write to " + output.getAbsolutePath() +  ": file(s) already exists. " 
					+ "Please specify a different output, i.e.: java -Xmx" + ( Runtime.getRuntime().maxMemory() / 900000) 
					+ "m -jar " + jar() + " " + alternativeOut(output, p, cmd));
		} else if (!cmd.contains("-bed") && !checkMinConfig(input)) {
			logger.error("Not enough memory: Please increase memory allocation, i.e.: java -Xmx" 
					+ (input.length() / 4000000) + "m -jar " + jar() + " " + cmd);
		} else {
			ok = true;
		}
		
		if (!ok) {
			ExitStatus.setStatus(2);
			throw new IllegalArgumentException();
		}
	}
	
	
	
	public static boolean forceSequentialRead(File file) {
		return MIN_PARALLEL_REQUIRED_FACTOR * file.length() > Runtime.getRuntime().maxMemory();
	}
	

	private static boolean checkPermissions(File file) {
		return (file.exists() && file.isDirectory() && file.canWrite()) || file.mkdirs();
	}
	
	
	private static boolean checkOutput(File file, String preffix) {
		return !(new File(file, preffix + Tags.SUMMARY_EXT).exists()) &&
			   !(new File(file, preffix + Tags.BREAKDOWN_EXT).exists()) &&
			   !(new File(file, preffix + Tags.COVERAGE_EXT).exists()) &&
			   !(new File(file, preffix + Tags.DETAILS_EXT).exists());
	}
	
	
	private static boolean checkMinConfig(File file) {
		return MIN_MEM_REQUIRED_FACTOR * file.length() <  Runtime.getRuntime().maxMemory();
	}
	

	private static String alternativeOut(File f, String p, String cmd) {
		if (cmd.contains(" " + p) && cmd.contains(" -n ")) {
			return cmd.replaceAll(p, generateOut(f, p));
		}
		return cmd + generateOut(f, p);
	}

	
	private static String generateOut(File f, String p) {
		if (checkOutput(f, p)) {
			return p;
		} else {
			return generateOut(f, p + "_0");
		}
	}
	
	
	private static String jar() {
		return ArgValidator.class.getProtectionDomain().getCodeSource().getLocation().getPath();
	}

}
