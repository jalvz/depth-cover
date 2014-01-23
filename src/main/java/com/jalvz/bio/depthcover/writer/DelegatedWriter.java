package com.jalvz.bio.depthcover.writer;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import org.apache.log4j.Logger;

public class DelegatedWriter {
	
	private static final Logger logger = Logger.getLogger(StatefulResultWriter.class.getName());
	
	private static final String SEPARATOR = "\t";
	
	private final FileWriter out;
	
	private StringBuilder content;

	private final int numColumns;
	

	protected DelegatedWriter(File out, List<String> header) throws IOException {
		out.createNewFile();
		this.out = new FileWriter(out, true);
		this.content = new StringBuilder("");
		this.numColumns = header.size();
		this.add(header);
	}

	
	
	public DelegatedWriter(File out, List<String> header, int c) throws IOException {
		out.createNewFile();
		this.out = new FileWriter(out, true);
		this.content = new StringBuilder(c);
		this.numColumns = header.size();
		this.add(header);
	}



	public void addLine(List<String> line) {
		if (line.size() != numColumns) {
			throw new IllegalArgumentException("Expected " + numColumns + " fields [got = " + line.size() + "]");
		}
		add(line);
	}
	
	
	public synchronized void write() {
		if (content.length() > 0) {
			try {
				out.write(toString());
				out.flush();
			} catch (IOException e1) {
				logger.debug(e1.getMessage(), e1);
				close();
				throw new UnsupportedOperationException("Severe: Not possible to write");
			}
			clear();
		}
	}
	

	public void clear() {
		this.content = content.delete(0, content.length());
	}

	
	public synchronized void close() {
		if (out != null) {
			try {
				out.close();
			} catch (IOException e) {
				logger.debug(e.getMessage(), e);
			}
		}
	}
	
	
	private void add(List<String> line) {
		for (String field : line) {
			content.append(field).append(SEPARATOR);
		}
		ret();
	}
	
	
	private void ret() {
		content.deleteCharAt(content.length() - 1);
		content.append("\n");
	}
	
	
	@Override
	public String toString() {
		return content.toString();
	}
	
}
