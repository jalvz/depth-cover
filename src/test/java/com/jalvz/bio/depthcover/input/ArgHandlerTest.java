package com.jalvz.bio.depthcover.input;

import static org.junit.Assert.*;

import java.io.File;

import org.junit.Test;

import com.google.common.collect.Lists;
import com.jalvz.bio.depthcover.input.ArgHandler;
import com.jalvz.bio.depthcover.reader.SampleFileReader;

public class ArgHandlerTest {

	
	@Test
	public void testHelp() {
		assertTrue(ArgHandler.getHelp(Lists.newArrayList("a", "b", "-h")));
		assertTrue(ArgHandler.getHelp(Lists.newArrayList("--help")));
		assertFalse(ArgHandler.getHelp(Lists.newArrayList("-b")));
	}

	
	@Test
	public void testFlag() {
		assertTrue(ArgHandler.getLowFlag((Lists.newArrayList("--ignore-index"))));
		assertFalse(ArgHandler.getLowFlag(Lists.newArrayList("-h")));
	}

	
	@Test
	public void testIn() {
		String bamPath = SampleFileReader.notIndexedFile().getAbsolutePath();
		File f = ArgHandler.getIn(Lists.newArrayList("-bam", bamPath, "-x"));
		assertTrue(f.getAbsolutePath().equals(bamPath));
	}

	
	@Test
	public void testDetails() {
		assertTrue(ArgHandler.getPrintDetailsFlag(Lists.newArrayList("-x", "-a")));
		assertTrue(ArgHandler.getPrintDetailsFlag(Lists.newArrayList("--all")));
		assertFalse(ArgHandler.getPrintDetailsFlag(Lists.newArrayList("--al")));
	}


	@Test(expected = IllegalArgumentException.class)
	public void testLimit() {
		assertTrue(ArgHandler.getDepthLimit(Lists.newArrayList("-x", "-l", "15")) == 15);
		ArgHandler.getDepthLimit(Lists.newArrayList("-x", "-l", "15a"));
	}
	

	@Test(expected = IllegalArgumentException.class)
	public void testOut() {
		String bamPath = SampleFileReader.notIndexedFile().getAbsolutePath();
		File f = ArgHandler.getOutDir(Lists.newArrayList("-bam", bamPath), SampleFileReader.notIndexedFile());
		assertTrue(f.isDirectory());
		
		File f2 = ArgHandler.getOutDir(Lists.newArrayList("-bam", bamPath, "-d", "/"), null);
		assertTrue(f2.isDirectory());
		
		ArgHandler.getOutDir(Lists.newArrayList("-bam", bamPath, "-d"), null);
	}


}
