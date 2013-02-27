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

import com.data2semantics.yasgui.server.SparqlService;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.query.ResultSetFormatter;


public class ObjectsFetcher {
	public static String QUERY = "SELECT DISTINCT ?class  {\n" + 
			"?s a ?class \n" + 
			"} \n" + 
			" LIMIT 1000";
	public static String CACHE_FILENAME = "objects.json";
	
//	public static String fetch(boolean forceUpdate, File cacheDir) {
	public static String fetch() {
		ResultSet resultSet = SparqlService.query("http://eculture2.cs.vu.nl:5020/sparql/", QUERY);
		return ResultSetFormatter.asText(resultSet);
	}
	
	public static void main(String[] args)  {
		System.out.println(ObjectsFetcher.fetch());
//		System.out.println(PropertiesFetcher.QUERY);
	}
}
