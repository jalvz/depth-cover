package com.jalvz.bio.depthcover.ds.queue;

import java.util.concurrent.ArrayBlockingQueue;

import com.jalvz.bio.depthcover.model.idata.IntervalDataBuilder;


public class CloseableQueue extends ArrayBlockingQueue<IntervalDataBuilder> implements Closeable {

	private static final long serialVersionUID = 1L;

	
	private boolean closed = false;
	
	
	public CloseableQueue(int arg0) {
		super(arg0);
	}


	@Override
	public void close() {
		closed = true;
	}

	
	@Override
	public boolean isClosed() {
		return closed;
	}
	
	
	public boolean notFinished(){
		return !(closed && super.isEmpty());
	}
	
	
	@Override
	public void put(IntervalDataBuilder e) throws InterruptedException {
		if (isClosed()) {
			throw new UnsupportedOperationException("Queue is closed, no more elements can be added.");
		}
		super.put(e);
	}
	
}
