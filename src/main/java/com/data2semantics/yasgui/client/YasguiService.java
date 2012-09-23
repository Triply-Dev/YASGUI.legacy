package com.data2semantics.yasgui.client;

import com.data2semantics.yasgui.shared.exceptions.PrefixFetchException;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath("YasguiService")
public interface YasguiService extends RemoteService {
	String fetchPrefixes(boolean forceUpdate) throws IllegalArgumentException, PrefixFetchException;
	String fetchEndpoints(boolean forceUpdate) throws IllegalArgumentException, PrefixFetchException;
}
