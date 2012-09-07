package com.data2semantics.yasgui.server;

import java.io.File;

import com.data2semantics.yasgui.client.YasguiService;
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
			throw new PrefixFetchException("Unable to fetch prefixes");
		}
		return prefixes;
	}
	
}
