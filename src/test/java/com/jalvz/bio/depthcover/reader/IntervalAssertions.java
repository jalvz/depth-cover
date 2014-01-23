package com.jalvz.bio.depthcover.reader;

import static org.junit.Assert.assertTrue;

import java.util.Map;

public class IntervalAssertions {

	
	public static void checkResults(Map<String, Map<Integer, Long>> rsmap) {
		assertTrue(rsmap.size() == 6);
		assertTrue(rsmap.containsKey("chr2 20160-20265 (match-48)"));
		assertTrue(rsmap.containsKey("chr3 105420-105429 (match-1-ini)"));
		assertTrue(rsmap.containsKey("chr3 105477-105482 (match-2-end)"));
		assertTrue(rsmap.containsKey("chr3 148574-148624 (match-29)"));
		assertTrue(rsmap.containsKey("chr3 155572-155582 (match-11)"));
		assertTrue(rsmap.containsKey("chrM 1-16569"));

		Map<Integer, Long> partial = rsmap.get("chr2 20160-20265 (match-48)");
		assertTrue(partial.size() == 1);
		assertTrue(partial.get(1) == 48);
		
		partial = rsmap.get("chr3 105420-105429 (match-1-ini)");
		assertTrue(partial.size() == 1);
		assertTrue(partial.get(1) == 1);

		partial = rsmap.get("chr3 105477-105482 (match-2-end)");
		assertTrue(partial.size() == 1);
		assertTrue(partial.get(1) == 2);

		partial = rsmap.get("chr3 148574-148624 (match-29)");
		assertTrue(partial.size() == 1);
		assertTrue(partial.get(1) == 29);

		partial = rsmap.get("chr3 155572-155582 (match-11)");
		assertTrue(partial.size() == 1);
		assertTrue(partial.get(1) == 11);

		partial = rsmap.get("chrM 1-16569");
		assertTrue(partial.size() == 3);		
		assertTrue(partial.get(1) == 1952);
		assertTrue(partial.get(2) == 565);
		assertTrue(partial.get(3) == 234);
	}
}
