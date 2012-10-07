/*******************************************************************************
 * Copyright (c)  2012 Laurens Rietveld
 * 
 *  Permission is hereby granted, free of charge, to any person
 *  obtaining a copy of this software and associated documentation
 *  files (the "Software"), to deal in the Software without
 *  restriction, including without limitation the rights to use,
 *  copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the
 *  Software is furnished to do so, subject to the following
 *  conditions:
 * 
 *  The above copyright notice and this permission notice shall be
 *  included in all copies or substantial portions of the Software.
 * 
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 *  EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
 *  OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 *  NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
 *  HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 *  WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 *  FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
 *  OTHER DEALINGS IN THE SOFTWARE.
 ******************************************************************************/
package com.data2semantics.yasgui.server.fetchers;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;

import org.json.JSONArray;

import com.data2semantics.yasgui.server.Helper;
import com.data2semantics.yasgui.server.SparqlService;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.RDFNode;


public class PropertiesFetcher {
	private static String VARNAME = "property";
	public static String CACHE_FILENAME = "properties.json";
	
	public static String fetch(boolean forceUpdate, File cacheDir) throws IOException {
		String result = "";
		JSONArray properties = new JSONArray();
		File file = new File(cacheDir + "/" + CACHE_FILENAME);
		boolean updateFile = forceUpdate;
		if (!updateFile) {
			if (!file.exists()) {
				updateFile = true;
			} else {
				Long now = new Date().getTime();
				Long lastModified = file.lastModified();
				if ((now - lastModified) > 1000 * 60 * 60 * 24 * 7) { //7 days cache
					updateFile = true;
				}
			}
		}
		if (updateFile) {
			ResultSet resultSet = SparqlService.query("http://eculture2.cs.vu.nl:5020/sparql/", getQuery());
			while (resultSet.hasNext()) {
				QuerySolution querySolution = resultSet.next();
				RDFNode rdfNode = querySolution.get(VARNAME);
				properties.put(rdfNode.asResource().getURI());
			}
			result = properties.toString();
			BufferedWriter out = new BufferedWriter(new FileWriter(file));
			out.write(result);
			out.close();
		} else {
			System.out.println("reading file on server");
			result = Helper.readFile(file);
		}
		return result;
	}
	
	public static String getQuery() {
		return "SELECT DISTINCT ?" + VARNAME + "\n" + 
				"WHERE {\n" + 
				"  ?s ?property ?o .\n" + 
				"}\n" +
				"ORDER BY ?property LIMIT 1000";
	}
	
	public static void main(String[] args)  {
//		System.out.println(PropertiesFetcher.fetch());
	}
}
