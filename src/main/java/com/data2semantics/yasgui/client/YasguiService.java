package com.data2semantics.yasgui.client;

import com.data2semantics.yasgui.shared.exceptions.FetchException;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath("YasguiService")
public interface YasguiService extends RemoteService {
	String fetchPrefixes(boolean forceUpdate) throws IllegalArgumentException, FetchException;
	String fetchEndpoints(boolean forceUpdate) throws IllegalArgumentException, FetchException;
}
