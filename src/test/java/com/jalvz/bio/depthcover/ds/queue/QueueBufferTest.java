package com.jalvz.bio.depthcover.ds.queue;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.jalvz.bio.depthcover.model.hdata.LazyHeaderDataFixture;
import com.jalvz.bio.depthcover.model.idata.IntervalDataFixture;

public class QueueBufferTest {

	@Test
	public void test() throws InterruptedException {
		CloseableQueue queue = new CloseableQueue(9);
		QueueBuffer qb = new QueueBuffer(queue, LazyHeaderDataFixture.getDictionary());
		qb.add(IntervalDataFixture.simple("chr2"));
		assertTrue(queue.isEmpty());
		qb.add(IntervalDataFixture.simple("chr3"));
		assertTrue(queue.isEmpty());
		qb.add(IntervalDataFixture.simple("chr1"));
		assertTrue(queue.size() == 3);
		qb.add(IntervalDataFixture.simple("chr5"));
		assertTrue(queue.size() == 3);
		qb.add(IntervalDataFixture.simple("chr8"));
		assertTrue(queue.size() == 3);
		qb.add(IntervalDataFixture.simple("chr4"));
		assertTrue(queue.size() == 5);
	}

}
