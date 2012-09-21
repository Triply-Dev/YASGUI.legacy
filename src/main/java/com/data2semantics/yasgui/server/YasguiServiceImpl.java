package com.data2semantics.yasgui.server;

import java.io.File;
import java.util.ArrayList;

import com.data2semantics.yasgui.client.YasguiService;
import com.data2semantics.yasgui.server.fetchers.EndpointsFetcher;
import com.data2semantics.yasgui.server.fetchers.PrefixesFetcher;
import com.data2semantics.yasgui.shared.exceptions.PrefixFetchException;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class YasguiServiceImpl extends RemoteServiceServlet implements YasguiService {
	public static String CACHE_DIR = "/cache";
	

	public String fetchPrefixes(boolean forceUpdate) throws IllegalArgumentException, PrefixFetchException {
		String prefixes = "";
		try {
			prefixes = PrefixesFetcher.fetch(forceUpdate, new File(getServletContext().getRealPath(CACHE_DIR))); 
		} catch (Exception e) {
			throw new PrefixFetchException("Unable to fetch prefixes", e);
		}
		return prefixes;
	}
	
	
	public ArrayList<String> fetchEndpointsAsArrayList(boolean forceUpdate) {
		int chunkSize = 60000;
		String endpointString = fetchEndpoints(forceUpdate);
		ArrayList<String> endpointArray = new ArrayList<String>();
		while (endpointString.length() > chunkSize) {
			endpointArray.add(endpointString.substring(0, chunkSize));
			endpointString = endpointString.substring(chunkSize+1);
		}
		return endpointArray;
	}
	
	public String fetchEndpoints(boolean forceUpdate) throws IllegalArgumentException, PrefixFetchException {
		String endpoints = "";
		try {
			endpoints = EndpointsFetcher.fetch(forceUpdate, new File(getServletContext().getRealPath(CACHE_DIR))); 
		} catch (Exception e) {
			throw new PrefixFetchException("Unable to fetch endpoints", e);
		}
//		System.out.println(Integer.toString(endpoints.length()) + endpoints);
//		endpoints = "";
//		for (int i = 0; i < 221561; i++) {
//		for (int i = 0; i < 71561; i++) {
//			endpoints += "b";
//		}
		return endpoints;
	}
}
