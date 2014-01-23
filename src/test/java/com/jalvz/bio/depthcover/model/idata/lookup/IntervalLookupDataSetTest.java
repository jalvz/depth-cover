package com.jalvz.bio.depthcover.model.idata.lookup;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.jalvz.bio.depthcover.model.idata.IntervalData;

public class IntervalLookupDataSetTest {

	IntervalLookupDataSet intervalLookupDataSet;
	
	@Before
	public void setup() {
		intervalLookupDataSet = new IntervalLookupDataSet();
		intervalLookupDataSet.add("chr2", 16, 27);
		intervalLookupDataSet.add("chr1", 16, 20);
		intervalLookupDataSet.add("chr1", 27, 32);
		intervalLookupDataSet.add("chr2", 9, 10);
		intervalLookupDataSet.add("chr1", 12, 15);
		intervalLookupDataSet.add("chr1", 4, 8);
		intervalLookupDataSet.add("chr1", 21, 25);
		intervalLookupDataSet.add("chr2", 2, 7);
		intervalLookupDataSet.add("chr1", 2, 3);
	}
	
	@Test
	public void testRefOverlaps() {		
		List<IntervalData> intersections1 = intervalLookupDataSet.intersectionsSE("sample1", "chr1", new int[]{8,13});
		assertTrue(intersections1.size() == 2);
		assertTrue(intersections1.get(0).getReferenceName().equals("chr1 4-8"));
		assertTrue(intersections1.get(1).getReferenceName().equals("chr1 12-15"));		
		assertTrue(intersections1.get(0).getOpenings().get(0) == 8);
		assertTrue(intersections1.get(0).getClosings().get(0) == 9);
		assertTrue(intersections1.get(1).getOpenings().get(0) == 12);
		assertTrue(intersections1.get(1).getClosings().get(0) == 14);

		List<IntervalData> intersections2 = intervalLookupDataSet.intersectionsSE("sample1", "chr2", new int[]{1,14});
		assertTrue(intersections2.size() == 2);
		assertTrue(intersections2.get(0).getReferenceName().equals("chr2 2-7"));
		assertTrue(intersections2.get(1).getReferenceName().equals("chr2 9-10"));
		assertTrue(intersections2.get(0).getOpenings().get(0) == 2);
		assertTrue(intersections2.get(0).getClosings().get(0) == 8);
		assertTrue(intersections2.get(1).getOpenings().get(0) == 9);
		assertTrue(intersections2.get(1).getClosings().get(0) == 11);
	}

	
	@Test
	public void testIntersections() {		
		int rs[] = intervalLookupDataSet.intersectionSE(new int[]{8,12}, new int[]{8,12});
		assertTrue(rs[0] == 8);
		assertTrue(rs[1] == 13);

		rs = intervalLookupDataSet.intersectionSE(new int[]{8,12}, new int[]{3,9});
		assertTrue(rs[0] == 8);
		assertTrue(rs[1] == 10);

		rs = intervalLookupDataSet.intersectionSE(new int[]{8,12}, new int[]{11,15});
		assertTrue(rs[0] == 11);
		assertTrue(rs[1] == 13);

		rs = intervalLookupDataSet.intersectionSE(new int[]{8,12}, new int[]{2,17});
		assertTrue(rs[0] == 8);
		assertTrue(rs[1] == 13);

		rs = intervalLookupDataSet.intersectionSE(new int[]{8,14}, new int[]{9,11});
		assertTrue(rs[0] == 9);
		assertTrue(rs[1] == 12);

		rs = intervalLookupDataSet.intersectionSE(new int[]{10,20}, new int[]{15,40});
		assertTrue(rs[0] == 15);
		assertTrue(rs[1] == 21);
	}

	
	
	@Test
	public void testReference() {		
		assertTrue(intervalLookupDataSet.getReferenceIntervalData("chr1").size() == 6);
		assertTrue(intervalLookupDataSet.getReferenceIntervalData("chr2").size() == 3);
		assertTrue(intervalLookupDataSet.getReferenceIntervalData("chr3").size() == 0);
	}
	
	
	@Test
	public void testContains() {
		assertTrue(intervalLookupDataSet.containsSL("chr1", new int[]{14,2}));
		assertTrue(intervalLookupDataSet.containsSL("chr1", new int[]{14,8}));
		assertTrue(intervalLookupDataSet.containsSL("chr1", new int[]{20,2}));
		assertTrue(intervalLookupDataSet.containsSL("chr1", new int[]{18,1}));
		assertTrue(intervalLookupDataSet.containsSL("chr1", new int[]{16,4}));
		assertFalse(intervalLookupDataSet.containsSL("chr1", new int[]{44,45}));
		assertFalse(intervalLookupDataSet.containsSL("chr2", new int[]{12,3}));
	}
	
	
	@Test(expected = IllegalArgumentException.class)
	public void testInvalid() {
		intervalLookupDataSet.intersectionSE(new int[]{21,15}, new int[]{1,15});
	}

}
