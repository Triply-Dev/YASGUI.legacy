package com.data2semantics.yasgui.server.fetchers.endpoints;

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
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONException;
import com.data2semantics.yasgui.server.Helper;
import com.hp.hpl.jena.rdf.model.RDFNode;

public class EndpointsFetcher {
	private final static Logger LOGGER = Logger.getLogger(EndpointsFetcher.class.getName()); 
	
	private static String CACHE_FILENAME = "endpoints.json";
	private static int CACHE_EXPIRES_DAYS = 300; //that should be more than enough
	
	public static String fetch(boolean forceUpdate, File cacheDir) throws JSONException, IOException {
		String result = "";
		if (!cacheDir.exists()) {
			cacheDir.mkdir();
			forceUpdate = true;
		}
		File cacheFile = new File(cacheDir + "/" + CACHE_FILENAME);
		
		if (forceUpdate || Helper.needUpdating(cacheFile, CACHE_EXPIRES_DAYS)) {
			try {
				result = fetchFromAny();
				Helper.writeFile(cacheFile, result);
			} catch (Exception e) {
				LOGGER.log(Level.WARNING, e.getMessage(), e);
				//couldnt fetch from any. if there is a cache file, just use that one (even if the user specified 'forceupdate'
			}
		} 
		if (result.length() == 0) {
			result = Helper.readFile(cacheFile);
		}
		return result;
	}
	
	/**
	 * Try fetching endpoints. 
	 * First try using mondeca -and- ckan (ckan for additional info, mondeca to filter only available endpoints)
	 * Then try using only ckan without filtering using mondeca
	 * If this fails as well, use mondeca (though we don't have additional endpoint info then)
	 * @return
	 * @throws IOException 
	 */
	private static String fetchFromAny() throws IOException {
		String result;
		try {
			result = MondecaAndCkanFetcher.fetch();
		} catch (Exception mondecaAndCkanException) {
			LOGGER.log(Level.WARNING, "Couldnt fetch from CKAN + Mondeca", mondecaAndCkanException);
			try {
				result = CkanFetcher.fetch();
			} catch (Exception ckanException) {
				LOGGER.log(Level.WARNING, ckanException.getMessage(), ckanException);
				try {
					result = MondecaFetcher.fetch();
				} catch (Exception mondecaException) {
					LOGGER.log(Level.WARNING, ckanException.getMessage(), ckanException);
					throw new IOException("Could not fetch endpoints from either CKAN and/or Mondeca...", mondecaException);
				}
			}
		}
		return result;
	}
	
	public static String getBindingValueAsString(RDFNode node) {
		if (node.isLiteral()) {
			return node.asLiteral().getString();
		} else if (node.isURIResource()) {
			return node.asResource().getURI();
		} else {
			return node.toString();
		}
	}
	
	public static void main(String[] args) {
		try {
			System.out.println(EndpointsFetcher.fetch(true, new File("bla2")).length());
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
