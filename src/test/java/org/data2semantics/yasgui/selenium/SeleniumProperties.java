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
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

public class SeleniumProperties extends Properties {
	private static final long serialVersionUID = 178585322470391365L;
	private Properties props;
	
	private static String KEY_MAIL_USERNAME = "mailUserName";
	private static String KEY_MAIL_PASSWORD = "mailPassword";
	private static String KEY_MAIL_SEND_TO = "mailSendTo";
	private static String KEY_SELENIUM_HOST = "checkHost";
	private static String KEY_SEND_MAIL = "sendMail";
				
	
	public SeleniumProperties() throws FileNotFoundException, IOException {
		props = new Properties();
		File propsFile = new File("bin/selenium/selenium.properties");
		if (propsFile.exists()) {
			props.load(new FileReader(propsFile));
		} else {
			System.out.println("running without a properties file!");
		}
	}
	
	public String getHostToTest() {
		if (props.contains(KEY_SELENIUM_HOST)) {
			return props.getProperty(KEY_SELENIUM_HOST);
		} else {
			return "http://dev.yasgui.laurensrietveld.nl";
		}
	}
	public String getMailUserName() {
		return props.getProperty(KEY_MAIL_USERNAME);
	}
	public String getMailPassWord() {
		return props.getProperty(KEY_MAIL_PASSWORD);
	}
	public String getMailSendTo() {
		return props.getProperty(KEY_MAIL_SEND_TO);
	}
	public boolean sendMail() {
		if (props.contains(KEY_SEND_MAIL)) {
			return props.getProperty(KEY_SEND_MAIL).equals("true");
		} else {
			return false;
		}
	}
}
