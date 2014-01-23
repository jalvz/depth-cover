package com.jalvz.bio.depthcover.writer;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.log4j.Logger;

import com.jalvz.bio.depthcover.ExitStatus;
import com.jalvz.bio.depthcover.util.Timer;

public class DoubleBufferAsyncWriter implements Runnable {

	private static final Logger logger = Logger.getLogger(DoubleBufferAsyncWriter.class.getName());

	private static final int BUFFER_SIZE = 3000;
	
	private final DelegatedWriter bufferedWriter0;
	
	private final DelegatedWriter bufferedWriter1;
	
	private int activeBuffer = 0;
	
	private int linesInBuffer = 0;
	
	private boolean lock = false;
	
	private boolean finished = false;
	
	
	protected DoubleBufferAsyncWriter(File out, List<String> header) throws IOException {
		bufferedWriter0 = new DelegatedWriter(out, header, BUFFER_SIZE * 50);
		bufferedWriter1 = new DelegatedWriter(out, header, BUFFER_SIZE * 50);
		bufferedWriter1.clear();
	}
	
	
	@Override
	public void run() {
		logger.debug("Started Writer thread - Id = " + Thread.currentThread().getId());
		while (!finished) {
			while (linesInBuffer < BUFFER_SIZE && !finished) {
				Timer.sleep(2);
			}
			try {
				flush();
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
				ExitStatus.setStatus(1);
				finished = true;
			}
		}
		logger.debug("Finished Writer thread - Id = " + Thread.currentThread().getId());
	}
	
	
	public synchronized void addLine(List<String> line) {
		while (lock) {
			logger.trace("waiting to buffer swap on async writer");
			Timer.sleep(1);
		}
		if (activeBuffer == 0) {
			bufferedWriter0.addLine(line);
		} else {
			bufferedWriter1.addLine(line);
		}
		linesInBuffer += 1;
	}

	
	public void finalise() {
		finished = true;
		flush();
		bufferedWriter0.close();
		bufferedWriter1.close();
	}
	
	

	private synchronized void flush() {
		acquireLock();
		if (linesInBuffer == 0) {
			releaseLock();
			return;
		}
		if (activeBuffer == 0) {
			swapBuffer();
			releaseLock();
			bufferedWriter0.write();
		} else {
			swapBuffer();
			releaseLock();
			bufferedWriter1.write();
		}
	}
	
	
	
	private void swapBuffer() {
		logger.trace("lines in writer buffer = " + linesInBuffer);
		activeBuffer = activeBuffer ^ 1;
		linesInBuffer = 0;
	}
		
	private void acquireLock() {
		lock = true;
	}
	
	private void releaseLock() {
		lock = false;
	}

}
