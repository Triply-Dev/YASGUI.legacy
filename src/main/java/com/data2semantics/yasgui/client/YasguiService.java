package com.data2semantics.yasgui.client;

import com.data2semantics.yasgui.shared.SparqlRuntimeException;
import com.data2semantics.yasgui.shared.rdf.ResultSetContainer;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath("YasguiService")
public interface YasguiService extends RemoteService {
  ResultSetContainer queryGetObject(String endpoint, String query) throws IllegalArgumentException, SparqlRuntimeException;
  String queryGetText(String endpoint, String query, String format) throws IllegalArgumentException, SparqlRuntimeException;
  
}
