package com.data2semantics.yasgui.server;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

public class Helper {
	public final static String readFile(File file) throws IOException {
		FileInputStream inputStream = new FileInputStream(file);
		FileChannel channel = inputStream.getChannel();
		ByteBuffer buffer = ByteBuffer.allocate((int) channel.size());
		channel.read(buffer);
		channel.close();
		inputStream.close();
		return new String(buffer.array());
	}
}
