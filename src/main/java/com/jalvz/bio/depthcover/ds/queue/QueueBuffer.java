package com.jalvz.bio.depthcover.ds.queue;

import java.util.List;
import java.util.Map;

import net.sf.samtools.SAMSequenceRecord;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.jalvz.bio.depthcover.model.idata.IntervalDataBuilder;

/**
 * Guarantees deliver in order
 */
public class QueueBuffer {
	
	private final CloseableQueue queue;
	
	private final Map<String, IntervalDataBuilder> buffer;
	
	private final List<String> priorityList;
	
	private int pointer;

	public QueueBuffer(CloseableQueue queue, List<SAMSequenceRecord> refChunks) {
		this.queue = queue;
		buffer = Maps.newHashMap();
		priorityList = Lists.newArrayList();
		pointer = 0;
		setupPriority(refChunks);
	}

	
	public void add(IntervalDataBuilder data) throws InterruptedException {
		if (inOrder(data)) {
			send(data);
			flushBuffer();
		} else {
			addToBuffer(data);
		}
	}
	
	
	
	private void flushBuffer() throws InterruptedException {
		if (priorityList.size() > pointer) {
			String nextRef = priorityList.get(pointer);
			if (buffer.containsKey(nextRef)) {
				send(buffer.remove(nextRef));
				flushBuffer();
			}
		}
	}

	
	private void send(IntervalDataBuilder data) throws InterruptedException {
		queue.put(data.build());
		pointer += 1;
	}

	
	private void addToBuffer(IntervalDataBuilder data) {
		buffer.put(data.getReferenceName(), data);
	}

	
	private boolean inOrder(IntervalDataBuilder data) {
		int index = priorityList.indexOf(data.getReferenceName());
		if (0 > index) {
			throw new IllegalArgumentException("Reference not expected [" + data.getReferenceName() + "]");
		}
		return index == pointer;
	}
	
	
	private void setupPriority(List<SAMSequenceRecord> refChunks) {
		for (SAMSequenceRecord ref : refChunks) {
			priorityList.add(ref.getSequenceName());
		}
	}
	

}
