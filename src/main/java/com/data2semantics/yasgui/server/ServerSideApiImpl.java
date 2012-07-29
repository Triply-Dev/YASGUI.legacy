package com.data2semantics.yasgui.server;

import com.data2semantics.yasgui.client.ServerSideApi;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class ServerSideApiImpl extends RemoteServiceServlet implements
    ServerSideApi {

  public String greetServer(String input) throws IllegalArgumentException {
    return "Hello, ";
  }

}
