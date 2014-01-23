package com.jalvz.bio.depthcover.writer;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.Lists;

import static com.jalvz.bio.depthcover.reader.SampleFileReader.recipient;

public class DelegatedWriterTest {

	File out;
	
	@Before
	public void setup() throws IOException {
		out = new File(recipient(), "test1.csv");
		out.createNewFile();
	}
	
	
	@Test
	public void testConstructor() throws IOException {
		DelegatedWriter writer = new DelegatedWriter(out, Lists.newArrayList("A","B","C"));
		String rs = writer.toString();
		assertTrue(rs.length() == 6);
		assertTrue(rs.contains("C"));
		assertTrue(rs.substring(rs.length() - 1).equals("\n"));
	}

	@Test(expected = IllegalArgumentException.class)
	public void testAdd() throws IOException {
		DelegatedWriter writer = new DelegatedWriter(out, Lists.newArrayList("A","B","C"));
		writer.addLine(Lists.newArrayList("a","b","c"));
		String rs = writer.toString();
		assertTrue(rs.length() == 12);
		assertTrue(rs.contains("a"));
		assertTrue(rs.substring(rs.length() - 1).equals("\n"));
		
		writer.addLine(Lists.newArrayList("a","b","c", "d"));
	}
	
	
	@Test(expected = IOException.class)
	public void testIOError() throws IOException {
		DelegatedWriter writer = new DelegatedWriter( new File(recipient(), "*/."), Lists.newArrayList("A","B","C"));
		writer.addLine(Lists.newArrayList("a","b","c"));
		writer.write();
	}
	
	
	
	@After
	public void teardown() {
		out.delete();
	}

}
