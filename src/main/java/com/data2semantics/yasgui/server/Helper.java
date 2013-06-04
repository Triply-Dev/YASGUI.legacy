package com.data2semantics.yasgui.server;

/*
 * #%L
 * YASGUI
 * %%
 * Copyright (C) 2013 Laurens Rietveld
 * %%
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 * #L%
 */

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Date;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.data2semantics.yasgui.shared.Bookmark;
import com.data2semantics.yasgui.shared.SettingKeys;

public class Helper {
	public final static void writeFile(File file, String content) throws IOException {
		file.createNewFile();
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
	
	public static boolean containsKey(JSONObject json, String key) throws JSONException {
		return (json.has(key) && json.getString(key).length() > 0);
	}
	
	public static ArrayList<Bookmark> getDefaultBookmarksFromConfig(JSONObject config) {
		ArrayList<Bookmark> list = new ArrayList<Bookmark>();
		try {
			JSONArray array = config.getJSONArray(SettingKeys.DEFAULT_BOOKMARKS);
			for (int i = 0; i < array.length(); i++) {
				JSONObject bookmarkObject = array.getJSONObject(i);
				Bookmark bookmark = new Bookmark();
				bookmark.setEndpoint(bookmarkObject.getString("endpoint"));
				bookmark.setQuery(bookmarkObject.getString("query"));
				bookmark.setTitle(bookmarkObject.getString("title"));
				list.add(bookmark);
			}
		} catch (Exception e) {
			//probably no default bookmarks defined
			e.printStackTrace();
		}
		
		return list;
	}
	
	public static String getAbsoluteRedirectPath(String endpointString, String redirect) throws URISyntaxException {
		if (redirect.startsWith("/")) {
			URI endpointUri = new URI(endpointString);
			return endpointUri.getScheme() + "://" + endpointUri.getHost() + redirect;
		} else {
			return redirect;
		}
	}
	public static void main(String[] args) throws URISyntaxException {
		System.out.println(getAbsoluteRedirectPath("http://bla.bla2.com/woei", "/redirect/blaredir"));
	}
}
