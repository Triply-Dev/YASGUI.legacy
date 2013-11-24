package com.data2semantics.yasgui.shared.autocompletions;

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

import java.io.Serializable;
import java.util.HashMap;

public class AutocompletionsInfo implements Serializable{
	public static int MAX_RETRIES = 5;
	
	private static final long serialVersionUID = -5811956075054566345L;
	HashMap<String, EndpointInfo> endpoints = new HashMap<String, EndpointInfo>();
	
	public EndpointInfo getEndpointInfo(String endpoint) {
		return endpoints.get(endpoint);
	}
	
	public EndpointInfo getOrCreateEndpointInfo(String endpoint) {
		EndpointInfo info;
		if (endpoints.containsKey(endpoint)) {
			info = endpoints.get(endpoint);
		} else {
			info = new EndpointInfo();
			info.setEndpoint(endpoint);
			endpoints.put(endpoint, info);
		}
		return info;
	}
	public void setEndpointInfo(EndpointInfo endpointInfo) {
		endpoints.put(endpointInfo.getEndpoint(), endpointInfo);
	}
	
	public boolean collectedEndpoint(String endpoint) {
		return endpoints.containsKey(endpoint);
	}
	
	public boolean coldAutocompletionFetch(String endpoint, String type) {
		boolean cold = false;
		if (endpoints.containsKey(endpoint)) {
			AutocompletionTypeInfo info = null;
			if (type.equals("property")) {
				info = endpoints.get(endpoint).getPropertyInfo();
			} else if (type.equals("class")){
				info = endpoints.get(endpoint).getClassInfo();
			}
			if (info != null) {
				cold = !info.hasAutocompletions() && info.isQueryResultsFetchingEnabled();
			}
		} else {
			cold = true;
		}
		return cold;
	}
	
	public boolean retryFetchAllowed(String endpoint, String type) {
		boolean retryAllowed = true;
		if (endpoints.containsKey(endpoint)) {
			AutocompletionTypeInfo info = null;
			if (type.equals("property")) {
				info = endpoints.get(endpoint).getPropertyInfo();
			} else if (type.equals("class")){
				info = endpoints.get(endpoint).getClassInfo();
			}
			if (info != null) {
				retryAllowed = info.retryAllowed();
			}
		}
		return retryAllowed;
	}
	public boolean queryAnalysisEnabled(String endpoint) {
		boolean enabled = !endpoint.contains("localhost");
		if (enabled && endpoints.containsKey(endpoint)) {
			enabled = endpoints.get(endpoint).getPropertyInfo().isQueryAnalysisEnabled() || endpoints.get(endpoint).getClassInfo().isQueryAnalysisEnabled();
		}
		return enabled;
	}
	
	public String toString() {
		String result = "List of endpoints: (" + endpoints.size() + ")\n";
		for (EndpointInfo info: endpoints.values()) {
			result += info.toString() + "\n";
		}
		return result;
	}
}
