package com.jalvz.bio.depthcover.reader;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;



public class SampleFileReader {

	public static File sampleFile() {
		return getFile("/sorted.bam");
	}
	
	public static File notIndexedFile() {
		return getFile("/not-indexed.bam");
	}
	
	public static File notOrderedFile() {
		return getFile("/not-sorted.bam");
	}

	public static File recipient() {
		return getFile("/");
	}
	
	public static File bedFile() {
		return getFile("/dummy.bed");
	}
	
	public static File simpleBed() {
		return getFile("/dummy2.bed");
	}


	public static File fastaFile() {
		return getFile("/dummy.fasta");
	}
	
	public static File badFasta() {
		return getFile("/bad.fasta");
	}
	
	public static URI getOutput() {
		URL url = SampleFileReader.class.getResource("/out.csv");
		try {
			return url.toURI();
		} catch (URISyntaxException e) {
			return null;
		}
	}
	
	
	private static File getFile(String name) {
		URL url = SampleFileReader.class.getResource(name);
		try {
			return new File(url.toURI());
		} catch (URISyntaxException e) {
			return null;
		}
	}
	
	
}
