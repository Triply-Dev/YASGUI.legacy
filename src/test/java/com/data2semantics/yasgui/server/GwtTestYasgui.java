package com.data2semantics.yasgui.server;

import com.google.gwt.junit.client.GWTTestCase;

/**
 * GWT JUnit <b>integration</b> tests must extend GWTTestCase.
 * Using <code>"GwtTest*"</code> naming pattern exclude them from running with
 * surefire during the test phase.
 * 
 * If you run the tests using the Maven command line, you will have to 
 * navigate with your browser to a specific url given by Maven. 
 * See http://mojo.codehaus.org/gwt-maven-plugin/user-guide/testing.html 
 * for details.
 */
public class GwtTestYasgui extends GWTTestCase {

  /**
   * Must refer to a valid module that sources this class.
   */
  public String getModuleName() {
    return "com.data2semantics.yasgui.YasguiJUnit";
  }


  public void testPrefixes() {
	  YasguiServiceImpl service = new YasguiServiceImpl();
	  String result = "";
	  try {
		  result = service.fetchPrefixes(true);
	  } catch (Exception e) {
		  fail("Request failure: " + e.getMessage());
	  }
	  assertTrue(result.length() > 0);
//    // Create the service that we will test.
//    GreetingServiceAsync greetingService = GWT.create(GreetingService.class);
//    ServiceDefTarget target = (ServiceDefTarget) greetingService;
//    target.setServiceEntryPoint(GWT.getModuleBaseURL() + "Yasgui/greet");
//
//    // Since RPC calls are asynchronous, we will need to wait for a response
//    // after this test method returns. This line tells the test runner to wait
//    // up to 10 seconds before timing out.
//    delayTestFinish(10000);
//
//    // Send a request to the server.
//    greetingService.greetServer("GWT User", new AsyncCallback<String>() {
//      public void onFailure(Throwable caught) {
//        // The request resulted in an unexpected error.
//        
//      }
//
//      public void onSuccess(String result) {
//        // Verify that the response is correct.
//        assertTrue(result.startsWith("Hello, GWT User!"));
//
//        // Now that we have received a response, we need to tell the test runner
//        // that the test is complete. You must call finishTest() after an
//        // asynchronous test finishes successfully, or the test will time out.
//        finishTest();
//      }
//    });
  }


}
