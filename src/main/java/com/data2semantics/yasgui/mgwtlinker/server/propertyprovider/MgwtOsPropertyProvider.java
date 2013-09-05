package com.data2semantics.yasgui.mgwtlinker.server.propertyprovider;

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

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import com.data2semantics.yasgui.mgwtlinker.server.BindingProperty;


public class MgwtOsPropertyProvider extends PropertyProviderBaseImpl {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3624651858511204668L;
	public static final BindingProperty iPhone = new BindingProperty("mgwt.os", "iphone");
	public static final BindingProperty retina = new BindingProperty("mgwt.os", "retina");
	public static final BindingProperty iPhone_undefined = new BindingProperty("mgwt.os", "iphone_undefined");

	public static final BindingProperty iPad = new BindingProperty("mgwt.os", "ipad");
	public static final BindingProperty iPad_retina = new BindingProperty("mgwt.os", "ipad_retina");
	public static final BindingProperty iPad_undefined = new BindingProperty("mgwt.os", "ipad_undefined");

	@Override
	public String getPropertyName() {
		return "mgwt.os";
	}

	@Override
	public String getPropertyValue(HttpServletRequest req) throws PropertyProviderException {
		String userAgent = getUserAgent(req);

		// android
		if (userAgent.contains("android")) {
			if (userAgent.contains("mobile")) {
				return "android";
			} else {
				return "android_tablet";
			}

		}

		if (userAgent.contains("ipad")) {
			String value = getRetinaCookieValue(req);
			if (value == null) {
				return "ipad_undefined";
			}

			if ("0".equals(value)) {
				return "ipad";
			}

			if ("1".equals(value)) {
				return "ipad_retina";
			}

		}

		if (userAgent.contains("iphone")) {
			String value = getRetinaCookieValue(req);
			if (value == null) {
				return "iphone_undefined";
			}

			if ("0".equals(value)) {
				return "iphone";
			}

			if ("1".equals(value)) {
				return "retina";
			}

		}

		if (userAgent.contains("blackberry")) {
			return "blackberry";
		}

		return "desktop";

	}

	public String getRetinaCookieValue(HttpServletRequest req) {

		Cookie[] cookies = req.getCookies();
		if (cookies == null)
			return null;

		for (int i = 0; i < cookies.length; i++) {
			Cookie cookie = cookies[i];
			if ("mgwt_ios_retina".equals(cookie.getName()))
				return (cookie.getValue());
		}
		return null;
	}
}
