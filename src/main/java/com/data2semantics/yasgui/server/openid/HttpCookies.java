package com.data2semantics.yasgui.server.openid;

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
import javax.servlet.http.HttpServletResponse;

/**
 * Created by IntelliJ IDEA.
 * User: aviadbendov
 * Date: Apr 19, 2008
 * Time: 11:06:32 PM
 */
public final class HttpCookies {
    public static final int dayInSeconds = 86400;

    public static Cookie findCookie(HttpServletRequest request, String name) {
        final Cookie[] cookies = request.getCookies();

        if (cookies == null) {
            return null;
        }

        for (Cookie cookie : cookies) {
            if (name.equals(cookie.getName())) {
                return cookie;
            }
        }

        return null;
    }

    public static String getCookieValue(HttpServletRequest request, String name) {
        Cookie cookie = findCookie(request, name);

        return cookie != null ? cookie.getValue() : null;
    }

    public static void resetCookie(HttpServletRequest request, HttpServletResponse response, String name) {
        Cookie cookie = findCookie(request, name);
        if (cookie != null) {
            cookie.setMaxAge(0);
            response.addCookie(cookie);
        }
    }

    public static void setCookie(HttpServletRequest request, HttpServletResponse response, final String cookieName, String cookieValue) {
        Cookie cookie = findCookie(request, cookieName);

        if (cookie == null) {
            cookie = new Cookie(cookieName, cookieValue);
        } else {
            cookie.setValue(cookieValue);
        }

        cookie.setMaxAge(dayInSeconds); // one day
        response.addCookie(cookie);
    }
}
