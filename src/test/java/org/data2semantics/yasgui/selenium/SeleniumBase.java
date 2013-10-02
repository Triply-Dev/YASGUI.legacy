package org.data2semantics.yasgui.selenium;

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
import java.util.Arrays;
import java.util.List;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Rule;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.openqa.selenium.server.RemoteControlConfiguration;
import org.openqa.selenium.server.SeleniumServer;

import com.thoughtworks.selenium.DefaultSelenium;
import com.thoughtworks.selenium.HttpCommandProcessor;
import com.thoughtworks.selenium.Selenium;

@RunWith(Parameterized.class)
public class SeleniumBase {

	protected static SeleniumServer server = null;
	protected static Selenium selenium = null;
	protected static HttpCommandProcessor proc = null;
	protected static boolean stopTesting = false;
	protected static SeleniumHelper helper;
	protected String runInBrowser;
	protected SeleniumProperties props;
	protected static String runningBrowser;

	public SeleniumBase(String browser) {
		this.runInBrowser = browser;
		System.out.println("constr running for browser " + browser);
	}

	@SuppressWarnings("rawtypes")
	@Parameters
	public static List data() {
		return Arrays.asList(new Object[][] { { "*firefox" }, { "*googlechrome" } });
	}

	@Before
	public void setUp() throws Exception {
		if (props == null) props = new SeleniumProperties();
		if (!stopTesting && runningBrowser != null && !runningBrowser.equals(runInBrowser)) {
			// we are have a different parameterized instantiation of junit. We
			// need to tear down all selenium objects,
			// and re-create them for this new browser
			stopSelenium();
		}

		if (server == null && selenium == null) {
			// i.e. only generate these objects when we need to!
			RemoteControlConfiguration config = new RemoteControlConfiguration();
			config.setUserExtensions(new File("bin/selenium/user-extensions.js"));
			server = new SeleniumServer(config);

			server.boot();

			proc = new HttpCommandProcessor("localhost", 4444, runInBrowser, props.getHostToTest());
			runningBrowser = runInBrowser;
			selenium = new DefaultSelenium(proc);
			helper = new SeleniumHelper(selenium, proc);
			selenium.start();
			selenium.windowMaximize();
			selenium.setSpeed("200");
		}
		

		if (!stopTesting) {
			selenium.open(props.getHostToTest());
			helper.assertNoErrorWindow();
		}
		
	}

	@AfterClass
	public static void stopSelenium() {
		if (selenium != null) {
			selenium.stop();
			selenium = null;
		}
		if (server != null) {
			server.stop();
			server = null;
		}
	}

	@Rule
	public FailHook watchman = new FailHook(this);

	@After
	public void tearDown() {
		if (selenium != null) {
			selenium.deleteAllVisibleCookies();
		}
	}
}
