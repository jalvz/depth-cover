package com.jalvz.bio.depthcover.analyser.calc;

import static org.junit.Assert.assertTrue;

import java.util.Map;

import org.junit.Test;

import com.jalvz.bio.depthcover.model.idata.IntervalDataFixture;

public class StandardCoverageCalculatorTest {

	private StandardCoverageCalculator calculator = new StandardCoverageCalculator();
	
	@Test
	public void testBasic() {
		Map<Integer, Long> result = calculator.calculate(IntervalDataFixture.custom(1,2,4,3)).getCoverage();
		assertTrue(result.size() == 1);
		assertTrue(result.get(1) == 5);
	}
	
	
	@Test
	public void testOverlap() {
		Map<Integer, Long> result = calculator.calculate(IntervalDataFixture.custom(1,5,3,5)).getCoverage();
		assertTrue(result.size() == 2);
		assertTrue(result.get(1) == 4);
		assertTrue(result.get(2) == 3);
	}
	
	@Test
	public void testFullOverlap() {
		Map<Integer, Long> result = calculator.calculate(IntervalDataFixture.custom(1,6,3,2)).getCoverage();
		assertTrue(result.size() == 2);
		assertTrue(result.get(1) == 4);
		assertTrue(result.get(2) == 2);
	}

	@Test
	public void test2StartAtSamePos() {
		Map<Integer, Long> result = calculator.calculate(IntervalDataFixture.custom(10,2,10,5)).getCoverage();
		assertTrue(result.size() == 2);
		assertTrue(result.get(1) == 3);
		assertTrue(result.get(2) == 2);
	}

	@Test
	public void test2EndAtSamePos() {
		Map<Integer, Long> result = calculator.calculate(IntervalDataFixture.custom(2,6,4,4)).getCoverage();
		assertTrue(result.size() == 2);
		assertTrue(result.get(1) == 2);
		assertTrue(result.get(2) == 4);
	}


	@Test
	public void test1Start1EndAtSamePos() {
		Map<Integer, Long> result = calculator.calculate(IntervalDataFixture.custom(5,2,7,2)).getCoverage();
		assertTrue(result.size() == 1);
		assertTrue(result.get(1) == 4);
	}


	@Test
	public void testComplex() {
		Map<Integer, Long> result = calculator.calculate(IntervalDataFixture.custom(1,3,2,6,4,1,4,3,5,4,7,3)).getCoverage();
		assertTrue(result.size() == 3);
		assertTrue(result.get(1) == 2);
		assertTrue(result.get(2) == 3);
		assertTrue(result.get(3) == 4);
	}

	
}
