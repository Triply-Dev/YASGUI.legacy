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

import javax.servlet.http.HttpServletRequest;

public class UserAgentPropertyProvider extends PropertyProviderBaseImpl {

  /**
	 * 
	 */
  private static final long serialVersionUID = 7773351123106881463L;

  @Override
  public String getPropertyValue(HttpServletRequest req) throws PropertyProviderException {

    String userAgent = getUserAgent(req);

    if (userAgent.contains("opera")) {
      return "opera";
    }

    if (userAgent.contains("safari") || userAgent.contains("iphone") || userAgent.contains("ipad")) {
      return "safari";
    }

    if (userAgent.contains("gecko")) {
      return "gecko1_8";
    }

    throw new PropertyProviderException("Can not find user agent property for userAgent: '" + userAgent + "'");

//    Firefox, all versions //gecko
//    All Webkit browsers (Safari, Chrome, Android Browser, etc) //safari
//    IE6 and IE7
//    IE8
//    IE9 (and IE10)
//    Opera //opera
  }

  @Override
  public String getPropertyName() {
    return "user.agent";
  }

}
