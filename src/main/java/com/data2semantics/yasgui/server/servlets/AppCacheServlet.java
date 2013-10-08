package com.data2semantics.yasgui.server.servlets;


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

import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.io.IOUtils;
import com.data2semantics.yasgui.shared.CookieKeys;

public class AppCacheServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	
	
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String strongName = getPermutationFromCookies(request.getCookies());
		if (strongName != null && strongName.length() > 0) {
			response.setHeader("Cache-Control", "no-cache");
			response.setHeader("Pragma", "no-cache");
			response.setDateHeader("Expires", new Date().getTime());
			response.setContentType("text/cache-manifest");
			
			
			String moduleDir = getServletContext().getRealPath("/Yasgui/");
			String appcacheFile = moduleDir + "/" + strongName;
			if (fetchManifestForDev(request)) {
				appcacheFile += ".dev";	
			}
			appcacheFile += ".appcache";
			
			PrintWriter out = response.getWriter();
			String manifestString = IOUtils.toString(new FileReader(appcacheFile));
			out.println(manifestString);
			out.close();
		}
		
	}

	private boolean fetchManifestForDev(HttpServletRequest request) {
		String type = request.getParameter("type");
		boolean fetchForDev = false;
		if (type != null && type.equals("dev")) fetchForDev = true;
		
		return fetchForDev;
	}

	private String getPermutationFromCookies(Cookie[] cookies) {
		String permutationStrongName = null;
		for (Cookie cookie: cookies) {
			if (cookie.getName().equals(CookieKeys.GWT_STRONG_NAME)) {
				permutationStrongName = cookie.getValue();
			}
		}
		return permutationStrongName;
		
	}

}
