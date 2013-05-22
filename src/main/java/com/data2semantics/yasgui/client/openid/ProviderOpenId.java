package com.data2semantics.yasgui.client.openid;

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

import com.data2semantics.yasgui.shared.StaticConfig;

public class ProviderOpenId implements OpenIdProvider{
	private static String NAME = "openid";
	private static String URL = null;
	private static boolean REQUIRE_USERNAME = false;
	private int MAX_ICON_HEIGHT = 60;
	public String getName() {
		return NAME;
	}

	public String getUrl() {
		return URL;
	}

	public boolean requireUsername() {
		return REQUIRE_USERNAME;
	}

	public String getImageLocation() {
		return StaticConfig.OPEN_ID_PROVIDER_IMG_PATH + NAME + ".png";
	}
	public int getMaxIconHeight() {
		return MAX_ICON_HEIGHT;
	}
}
