package com.jalvz.bio.depthcover.ds.queue;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import com.jalvz.bio.depthcover.model.idata.IntervalDataBuilder;

public class ClosableQueueTest {

	CloseableQueue queue;
	
	@Before
	public void setup() {
		queue = new CloseableQueue(3);
	}
	
	@Test
	public void testClosingBehaviour() throws InterruptedException {
		assertFalse(queue.isClosed());
		queue.put(new IntervalDataBuilder("", "", 0, 10).addIntervalSL(new int[]{1,1}));
		assertFalse(queue.isClosed());
		queue.close();
		assertTrue(queue.isClosed());
	}

	@Test
	public void testFinished() throws InterruptedException {
		assertTrue(queue.notFinished());
		
		queue.put(new IntervalDataBuilder("", "", 0, 10).addIntervalSL(new int[]{1,1}));
		queue.poll();
		assertTrue(queue.notFinished());
		
		queue.put(new IntervalDataBuilder("", "", 0, 10).addIntervalSL(new int[]{1,1}));
		queue.close();
		assertTrue(queue.notFinished());

		queue.poll();
		assertFalse(queue.notFinished());
	}

	
	@Test(expected = UnsupportedOperationException.class)
	public void testAddWhenClosed1() throws InterruptedException {
		queue.put(new IntervalDataBuilder("", "", 0, 10).addIntervalSL(new int[]{1,1}));
		queue.close();
		queue.put(new IntervalDataBuilder("", "", 0, 10).addIntervalSL(new int[]{1,1}));
	}
	

}
