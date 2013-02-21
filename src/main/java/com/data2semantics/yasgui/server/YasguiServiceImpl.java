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
package com.data2semantics.yasgui.server;

import java.io.File;
import com.data2semantics.yasgui.client.YasguiService;
import com.data2semantics.yasgui.server.fetchers.EndpointsFetcher;
import com.data2semantics.yasgui.server.fetchers.PrefixesFetcher;
import com.data2semantics.yasgui.shared.exceptions.FetchException;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class YasguiServiceImpl extends RemoteServiceServlet implements YasguiService {
	public static String CACHE_DIR = "/cache";
	
	public String fetchPrefixes(boolean forceUpdate) throws IllegalArgumentException, FetchException {
		String prefixes = "";
		try {
			prefixes = PrefixesFetcher.fetch(forceUpdate, new File(getServletContext().getRealPath(CACHE_DIR))); 
		} catch (Exception e) {
			throw new FetchException("Unable to fetch prefixes", e);
		}
		return prefixes;
	}
	
	
	public String fetchEndpoints(boolean forceUpdate) throws IllegalArgumentException, FetchException {
		String endpoints = "";
		try {
			endpoints = EndpointsFetcher.fetch(forceUpdate, new File(getServletContext().getRealPath(CACHE_DIR))); 
		} catch (Exception e) {
			throw new FetchException("Unable to fetch endpoints: " + e.getMessage() + "\n", e);
		}
		return endpoints;
	}
}
