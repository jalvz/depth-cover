package com.jalvz.bio.depthcover.reader.async;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import org.apache.log4j.Logger;

import com.jalvz.bio.depthcover.util.Timer;

public abstract class SafeAsyncReader<T> implements Runnable {
	
	private static final Logger logger = Logger.getLogger(SafeAsyncReader.class.getName());

	private boolean finished = false;

	protected BufferedReader currentReaderObj;

	@Override
	public void run() {
		logger.debug("Started Ref/Interval Reader thread - Id = " + Thread.currentThread().getId());
		if (!finished) {
			try {
				read();
			} catch (IOException e) {
				logger.debug(e.getMessage(), e);
				throw new UnsupportedOperationException("Severe: not possible to read");
			} finally {
				finished = true;
				closeCurrentResource();
			}
		}
		finished = true;
		logger.debug("Finished Ref/Interval Reader thread - Id = " + Thread.currentThread().getId());
	}
	

	/**
	 * Blocking call
	 */
	public abstract T get();
	
	
	protected void forceComputation() {
		while (!finished) {
			logger.debug("Waiting on async read - Thread id = " + Thread.currentThread().getId());
			Timer.sleep(1000);
		}
	}
	
	
	protected abstract void read() throws IOException;

	
	protected void initResource(File file) throws FileNotFoundException {
		currentReaderObj = new BufferedReader(new FileReader(file));
	}
	
	
	
	private void closeCurrentResource() {
		if (currentReaderObj != null) {
			try {
				currentReaderObj.close();
			} catch (IOException e) {
				logger.debug(e.getMessage(), e);
			}
		}
	}
	
}
