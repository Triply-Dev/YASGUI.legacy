package com.data2semantics.yasgui.server;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

import junit.framework.TestCase;

public class YasguiServiceTests extends TestCase {
	private File testDir = new File("junitTest");

	protected void setUp() {
		testDir.mkdir();
	}

	protected void tearDown() {
		final File[] files = testDir.listFiles();
		for (File f: files) f.delete();
		testDir.delete();
	}

	public void testPrefixFetching() throws FileNotFoundException {
		String result = "";
		result = Prefixes.fetch(false, testDir);
		assertTrue("Empty result set returned", result.length() > 0);
		File cacheFile = new File(testDir.getAbsolutePath() + "/" + Prefixes.CACHE_FILENAME);
		assertTrue("No cache file created", cacheFile.exists());
		
		PrintWriter writer = new PrintWriter(cacheFile);
		writer.print("");
		writer.close();
		result = Prefixes.fetch(false, testDir);
		assertTrue("Result set should be empty, but is: " + Integer.toString(result.length()), result.length() == 0);
		
		result = Prefixes.fetch(true, testDir);
		assertTrue("Forcing update, so should have a cache file with content", result.length() > 0);
	}
	
	

}
