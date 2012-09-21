package com.data2semantics.yasgui.server;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Date;

public class Helper {
	public final static void writeFile(File file, String content) throws IOException {
		BufferedWriter out = new BufferedWriter(new FileWriter(file));
		out.write(content);
		out.close();
	}
	
	public final static String readFile(File file) throws IOException {
		FileInputStream inputStream = new FileInputStream(file);
		FileChannel channel = inputStream.getChannel();
		ByteBuffer buffer = ByteBuffer.allocate((int) channel.size());
		channel.read(buffer);
		channel.close();
		inputStream.close();
		return new String(buffer.array());
	}

	public static String getExceptionStackTraceAsString(Exception exception) {
		StringWriter sw = new StringWriter();
		exception.printStackTrace(new PrintWriter(sw));
		return sw.toString();
	}

	/**
	 * Checks whether we need to update a file
	 * 
	 * @param file
	 * @param expire The number of days after the file expires
	 * 
	 * @return
	 */
	public static boolean needUpdating(File file, int expire) {
		boolean updateFile = false;
		if (!file.exists()) {
			updateFile = true;
		} else {
			Long now = new Date().getTime();
			Long lastModified = file.lastModified();
			if ((now - lastModified) > 1000 * 60 * 60 * 24 * expire) {
				updateFile = true;
			}
		}
		return updateFile;
	}

}
