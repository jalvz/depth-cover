package com.jalvz.bio.depthcover.writer;

import java.io.File;
import java.io.IOException;

import com.google.common.collect.Lists;

public class DoubleBufferAsyncWriterFixture {
	
	public static DoubleBufferAsyncWriter initAndGetAsyncWriter(File f) {
		DoubleBufferAsyncWriter writer;
		try {
			writer = new DoubleBufferAsyncWriter(f, Lists.newArrayList("A", "B", "C"));
			new Thread(writer).start();
			return writer;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

}
