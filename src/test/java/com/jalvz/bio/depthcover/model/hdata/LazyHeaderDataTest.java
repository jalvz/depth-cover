package com.jalvz.bio.depthcover.model.hdata;

import static org.junit.Assert.*;

import org.junit.Test;

public class LazyHeaderDataTest {

	@Test
	public void testHeader() {
		LazyHeaderData header = LazyHeaderDataFixture.getHeader();
		assertTrue(header.getTotal() == 3095693981l);
		assertTrue(header.get("chr1") == 249250621l);
		assertTrue(header.get("chrM") == 16569l);

	}
	
	@Test
	public void test() {
		assertTrue(LazyHeaderData.getAveragesHeadline().size() == 6);
		assertTrue(LazyHeaderData.getDetailsHeadline().size() == 3);
		assertTrue(LazyHeaderData.getGlobalHeadline().size() == 5);
		assertTrue(LazyHeaderData.getReferenceHeadline().size() == 6);
	}

}
