package com.data2semantics.yasgui.shared;

import java.io.Serializable;

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

public class UserDetails implements Serializable {
	private static final long serialVersionUID = -7506950527106295667L;
	private String cookieName;
	private String uniqueOpenId;
	public void setCookieName(String cookieName) {
		this.cookieName = cookieName;
		
	}
	public void setUniqueOpenId(String uniqueOpenId) {
		this.uniqueOpenId = uniqueOpenId;
	}
	
	public String getCookieName() {
		return cookieName;
	}
	public String getUniqueOpenId() {
		return uniqueOpenId;
	}
	
	public boolean isLoggedIn() {
		return (getCookieName() != null && getUniqueOpenId() != null);
	}
	
	public String toString() {
		return "uniqueOid: " + uniqueOpenId + "\n" + "cookie name: " + cookieName;
	}
}
