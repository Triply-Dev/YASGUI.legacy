package com.data2semantics.yasgui.server.fetchers;

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
