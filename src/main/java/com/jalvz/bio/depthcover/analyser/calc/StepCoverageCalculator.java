package com.jalvz.bio.depthcover.analyser.calc;

import com.google.common.collect.Lists;
import com.jalvz.bio.depthcover.writer.DoubleBufferAsyncWriter;
import static com.jalvz.bio.depthcover.util.WriterUtils.toStr;

public class StepCoverageCalculator extends StandardCoverageCalculator {

	private final DoubleBufferAsyncWriter writer;
	
	
	public StepCoverageCalculator(DoubleBufferAsyncWriter asyncWriter) {
		writer = asyncWriter;
	}


	@Override
	protected void send(String ref, int depth, int from, int to) {
		for (int idx = from; idx < to; idx++) {
			writer.addLine(Lists.newArrayList(ref, toStr(idx), toStr(depth)));
		}
	}
	
	
}
