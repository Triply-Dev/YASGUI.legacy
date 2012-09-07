package com.data2semantics.yasgui.server;

import java.io.File;

import com.data2semantics.yasgui.client.YasguiService;
import com.data2semantics.yasgui.client.settings.Settings;
import com.data2semantics.yasgui.server.fetchers.PrefixesFetcher;
import com.data2semantics.yasgui.shared.exceptions.SparqlException;
import com.data2semantics.yasgui.shared.rdf.ResultSetContainer;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.hp.hpl.jena.query.ResultSet;

/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class YasguiServiceImpl extends RemoteServiceServlet implements YasguiService {
	public static String CACHE_DIR = "/cache";
	

	public String fetchPrefixes(boolean forceUpdate) throws IllegalArgumentException {
		return PrefixesFetcher.fetch(forceUpdate, new File(getServletContext().getRealPath(CACHE_DIR)));
	}
	
}
