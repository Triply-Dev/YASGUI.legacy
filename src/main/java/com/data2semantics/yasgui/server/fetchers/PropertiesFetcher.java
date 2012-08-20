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
