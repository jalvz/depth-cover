package com.jalvz.bio.depthcover.model.idata;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class IntervalDataBuilderTest {

	IntervalDataBuilder intervalDataBuilder;
	
	@Before
	public void setup() {
		intervalDataBuilder = new IntervalDataBuilder("sample1", "chr1", 0, 10);
	}
	
	@Test
	public void testValidation() {
		assertFalse(intervalDataBuilder.isValid());
		intervalDataBuilder.addIntervalSL(new int[]{1,2});
		assertTrue(intervalDataBuilder.isValid());
	}

	@Test
	public void testLog() {
		intervalDataBuilder.addIntervalSL(new int[]{1,2});
		intervalDataBuilder.addIntervalSL(new int[]{3,4});
		assertTrue(intervalDataBuilder.toString().contains("sample1"));
		assertTrue(intervalDataBuilder.toString().contains("chr1"));
		assertTrue(intervalDataBuilder.toString().contains("reads"));
	}
	
	@Test(expected = IllegalStateException.class) 
	public void testInvalidBuild() {
		intervalDataBuilder.build();
	}

	@Test
	public void testBuild() {
		int[] first = new int[]{1,2};
		intervalDataBuilder.addIntervalSL(first);
		intervalDataBuilder.addIntervalSL(new int[]{10,20});
		intervalDataBuilder.addIntervalSL(new int[]{11,22});
		IntervalData data = intervalDataBuilder.build();
		assertTrue(data.getSampleName().equals("sample1"));
		assertTrue(data.getReferenceName().equals("chr1"));
		assertTrue(data.getOpenings().size() == 3);
		assertTrue(data.getOpenings().contains(1));
		assertTrue(data.getClosings().size() == 3);
		assertTrue(data.getClosings().contains(3));
		assertTrue(data.getStart() == 0);
		assertTrue(data.getEnd() == 10);
		assertTrue(data.getTotalLenght() == 44);
	}

}
