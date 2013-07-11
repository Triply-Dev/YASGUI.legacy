package com.data2semantics.yasgui.server.fetchers;

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
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import org.json.JSONArray;
import com.data2semantics.yasgui.server.Helper;
import com.data2semantics.yasgui.server.SparqlService;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.RDFNode;


public class PropertiesFetcher {
	private static String VARNAME = "property";
	private static String QUERY_EXPENSIVE = "SELECT DISTINCT ?property\n" + 
			"WHERE {\n" + 
			"  ?s ?property ?o .\n" + 
			"}\n" +
			"";
	private static String QUERY_CHEAP = "SELECT DISTINCT ?property\n" + 
			"WHERE {\n" + 
			"  ?s ?property ?o .\n" + 
			"}\n" +
			"";
	public static String CACHE_BASENAME = "props";
	private static int CACHE_EXPIRES_DAYS = 360;
	public static String fetch(String endpoint, boolean forceUpdate, File cacheDir) throws IOException {
		String result = "";
		System.out.println("before cache dir create");
		if (!cacheDir.exists()) {
			boolean bool = cacheDir.mkdir();
			if (!bool) {
				System.out.println("could not make cache dir!!");
			}
			forceUpdate = true;
		}
		
		File file = new File(cacheDir + "/" + CACHE_BASENAME + "_" + endpoint.replace(File.separator, "_") + ".json");
		if (forceUpdate || Helper.needUpdating(file, CACHE_EXPIRES_DAYS)) {
			System.out.println("updating file");
			file.createNewFile();
			JSONArray properties = tryFetches(endpoint);
			if (properties.length() > 0) {
				result = properties.toString();
				Helper.writeFile(file, result);
			}
			
		} else {
			try {
				System.out.println("reading file");
				result = Helper.readFile(file);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return result;
	}
	
	private static JSONArray tryFetches(String endpoint) {
		System.out.println("try fetches");
		JSONArray properties = new JSONArray();
		try {
			System.out.println("fetching expensive");
			properties = getProperties(endpoint, QUERY_EXPENSIVE);
		} catch(Exception e) {
			try {
				System.out.println("fetching cheap");
				properties = getProperties(endpoint, QUERY_CHEAP);
			} catch (Exception e2) {
				e2.printStackTrace();
				//no other options.. probably just a typo in the endpoint or something
			}
		}
		return properties;
	}
	
	private static JSONArray getProperties(String endpoint, String query) {
		ArrayList<String> properties = new ArrayList<String>();
		ResultSet resultSet = SparqlService.query(endpoint, query);
		while (resultSet.hasNext()) {
			QuerySolution querySolution = resultSet.next();
			RDFNode rdfNode = querySolution.get(VARNAME);
			properties.add(rdfNode.asResource().getURI());
		}
		
		//we are sorting ourselves instead of letting SPARQL do this
		//There can be quite a bit of difference in response time when using ORDER BY w.r.t. not ordering
		//I want to avoid getting SPARQL execution timeouts, so just do this part server-side
		Collections.sort(properties);
		JSONArray jsonProperties = new JSONArray();
		for (String property: properties) {
			jsonProperties.put(property);
		}
		
		return jsonProperties;
	}
	
	
	public static void main(String[] args) throws IOException  {
		System.out.println(PropertiesFetcher.fetch("http://localhost:8080/openrdf-workbench/repositories/sp2b/query", false, new File("/home/lrd900/code/yasgui/target/yasgui-12.10/cache")));
	}
}
