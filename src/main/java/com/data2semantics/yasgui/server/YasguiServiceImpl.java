package com.data2semantics.yasgui.server;

import com.data2semantics.yasgui.client.YasguiService;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class YasguiServiceImpl extends RemoteServiceServlet implements
    YasguiService {

  public String greetServer(String input) throws IllegalArgumentException {
    return "Hello, ";
  }
  
  public String query() {
	  return "query";
  }

}
