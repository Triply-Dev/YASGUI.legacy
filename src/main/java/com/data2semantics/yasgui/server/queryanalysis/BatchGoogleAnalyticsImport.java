package com.data2semantics.yasgui.server.queryanalysis;

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

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import au.com.bytecode.opencsv.CSVReader;

import com.data2semantics.yasgui.server.Helper;
import com.data2semantics.yasgui.server.QueryPropertyExtractor;
import com.data2semantics.yasgui.server.db.DbHelper;
import com.data2semantics.yasgui.shared.autocompletions.AccessibilityStatus;
import com.data2semantics.yasgui.shared.autocompletions.FetchMethod;
import com.data2semantics.yasgui.shared.autocompletions.FetchType;
import com.hp.hpl.jena.query.QueryParseException;
import com.hp.hpl.jena.sparql.expr.ExprException;

public class BatchGoogleAnalyticsImport {
	private File file;
	private DbHelper dbHelper;
	private Map<String, Boolean> accessibleEndpoints = new HashMap<String, Boolean>();
	private int debugCsvRow;
	public BatchGoogleAnalyticsImport(DbHelper dbhelper, String file, int debugCsvRow) throws IOException {
		this.file = new File(file);
		if (!this.file.exists()) {
			throw new IOException("File not found");
		}
		this.dbHelper = dbhelper;
		this.debugCsvRow = debugCsvRow;
	}
	
	
	
	private void readAndAnalyze() throws Throwable {
		System.out.println("reading file " + file.getAbsolutePath());
		CSVReader csvReader = new CSVReader(new FileReader(file), ','); 
		
		int queryCol = 0;
		int endpointCol = 1;
		int count = -1;
		String[] line;
		while ((line = csvReader.readNext()) != null) {
			count++;
			boolean debug = count == debugCsvRow;
			System.out.println(""+count);
			if (count < debugCsvRow) continue;
			if (count == debugCsvRow) debug = true;
			
			
			if (endpointCol < line.length) {
				
				String endpoint = line[endpointCol];
				if (debug) {
					System.out.println(endpoint);
				}
				if (endpoint.startsWith("http") && !endpoint.contains("localhost") && dbHelper.autocompletionFetchingEnabled(endpoint, FetchType.PROPERTIES, FetchMethod.QUERY_ANALYSIS)) {
					
					String query = line[queryCol];
					if (debug) {
						System.out.println(query);
					}
					boolean accessible = false;
					if (!accessibleEndpoints.containsKey(endpoint)) {
						if (debug) {
							System.out.println("not in accessible hashmap");
						}
						accessibleEndpoints.put(endpoint, Helper.checkEndpointAccessibility(endpoint) == AccessibilityStatus.ACCESSIBLE);
						if (Helper.checkEndpointAccessibility("http://google.com") != AccessibilityStatus.ACCESSIBLE) {
							System.out.println("connection lost...");
							System.exit(1);
						}
//						///////test!
//						accessible = accessibleEndpoints.get(endpoint);
//						System.out.println(endpoint + ": " + (accessible? "accessible": "inaccessible"));
					}
					accessible = accessibleEndpoints.get(endpoint);
					if (accessible && query.length() > 0) {
						if (debug) {
							System.out.println("accessible");
						}
						try {
							QueryPropertyExtractor.store(dbHelper, query, endpoint, debug);
						} catch (QueryParseException e) {
							if (debug) {
								e.printStackTrace();
							}
							//just ignore this query
						} catch (ExprException e) {
							if (debug) {
								e.printStackTrace();
							}
							//just ignore regex probs
						} catch (Exception e2) {
							csvReader.close();
							System.out.println(e2.getClass());
							System.out.println(query);
							throw e2;
						}
					}
				}
			}
		}
		csvReader.close();
	}
	
	
	
	public static void doImport(DbHelper dbHelper, String file) throws Throwable {
		doImport(dbHelper, file, -1);
	}
	
	public static void doImport(DbHelper dbHelper, String file, int debugCsvRow) throws Throwable {
		BatchGoogleAnalyticsImport gaImport = new BatchGoogleAnalyticsImport(dbHelper, file, debugCsvRow);
		gaImport.readAndAnalyze();
	}
	
	



	public static void main(String[] args) throws Throwable {
		DbHelper dbHelper = new DbHelper(new File("src/main/webapp/"));
//		String[] inputFiles = new String[]{"page1.csv","page2.csv","page3.csv"};
		String[] inputFiles = new String[]{"page1.csv"};
		for (String file: inputFiles) {
			doImport(dbHelper, file);
		}
	}
}
