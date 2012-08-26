package com.data2semantics.yasgui.client;

import com.data2semantics.yasgui.shared.Settings;
import com.data2semantics.yasgui.shared.exceptions.SparqlException;
import com.data2semantics.yasgui.shared.rdf.ResultSetContainer;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath("YasguiService")
public interface YasguiService extends RemoteService {
  ResultSetContainer queryGetObject(Settings settings) throws IllegalArgumentException, SparqlException;
  String queryGetText(Settings settings) throws IllegalArgumentException, SparqlException;
  String fetchPrefixes(boolean forceUpdate) throws IllegalArgumentException;
}
