package com.jalvz.bio.depthcover.analyser.calc;


import com.jalvz.bio.depthcover.model.cdata.CoverageData;
import com.jalvz.bio.depthcover.model.idata.IntervalData;

public interface CoverageCalculator {

	/**
	 * Performs depth of coverage computation
	 * @param intervalData actual input of sequences identified by <start position (included), sequence lenght>
	 * @return coverageData contains a map with <depth of coverage, number of bases> values
	 */
	CoverageData calculate(IntervalData intervalData);
	
}
