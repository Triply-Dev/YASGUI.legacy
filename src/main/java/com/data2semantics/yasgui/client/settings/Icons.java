package com.data2semantics.yasgui.client.settings;

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

public class Icons {
	public static String ADD_TAB = "icons/nounproject/addPage.png";
	public static String ADD = "icons/nounproject/add.png";
	public static String BOOKMARK_QUERY = "icons/nounproject/bookmarkPage.png";
	public static String SHOW_BOOKMARKS = "icons/nounproject/bookmarksWhiteBg.png";
	public static String CHECKBOX = "icons/nounproject/checkbox.png";
	public static String CHECKMARK = "icons/nounproject/checkMark.png";
	public static String CHECK_CROSS = "icons/nounproject/checkCross.png";
	public static String COPY_TAB = "icons/nounproject/copy.png";
	public static String CROSS = "icons/nounproject/cross.png";
	public static String DOWNLOAD = "icons/nounproject/download.png";
	public static String INFO = "icons/nounproject/info.png";
	public static String LINK = "icons/nounproject/link.png";
	public static String REFRESH = "icons/nounproject/refresh.png";
	public static String SEARCH = "icons/nounproject/search.png";
	public static String TABLE = "icons/nounproject/table.png";
	public static String EDIT_TEXT = "icons/nounproject/editText.png";
	public static String TEXT = "icons/nounproject/text.png";
	public static String TOOLS = "icons/nounproject/tools.png";
	public static String TOOLTIP = "icons/nounproject/tooltip.png";
	public static String WARNING = "icons/nounproject/warning.png";
	public static String LOADING = "icons/custom/ajax_loader.gif";
	public static String QUERY_ERROR = "icons/nounproject/playSquareError.png";
	public static String EXECUTE_QUERY = "icons/nounproject/playSquare.png";
	public static String LOG_OUT = "icons/nounproject/logOut.png";
	public static String LOG_IN = "icons/nounproject/logIn.png";
	public static String QUESTION_MARK = "icons/nounproject/questionMark.png";
	
	public static String LOGO_GITHUB = "logos/github.jpg";
	
	public static String DIR_IMAGES = "images/";
	public static String getDisabled(String icon) {
		String extension = "";
		int lastDot = icon.lastIndexOf('.');
		if (lastDot > 0) {
			extension = icon.substring(lastDot + 1);
			icon = icon.substring(0, lastDot);
		}
		int lastSlash = icon.lastIndexOf("/");
		String path = "";
		String basename = icon;
		if (lastSlash > 0) {
			basename = icon.substring(lastSlash + 1);
			path = icon.substring(0, lastSlash);
		}
		return path + "/" + basename + "_Disabled." + extension;
	}
}
