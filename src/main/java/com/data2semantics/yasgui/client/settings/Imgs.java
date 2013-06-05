package com.data2semantics.yasgui.client.settings;

import com.data2semantics.yasgui.shared.StaticConfig;

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

public class Imgs {
	
	public static String OUTPUT_TABLE = "outputFormats/table.png";
	public static String OUTPUT_TABLE_SIMPLE = "outputFormats/simpleTable.png";
	public static String OUTPUT_RAW = "outputFormats/rawResponse.png";
	public static String CLOSE_TAB_SINGLE = "other/close-one.png";
	public static String CLOSE_TAB_OTHERS = "other/close-others.png";
	public static String CLOSE_TAB_ALL = "other/close-all.png";
	public static String ADD_TAB = "nounproject/addPage.png";
	public static String ADD = "nounproject/add.png";
	public static String BOOKMARK_QUERY = "nounproject/bookmarkPage.png";
	public static String SHOW_BOOKMARKS = "nounproject/bookmarks.png";
	public static String CHECKBOX = "nounproject/checkbox.png";
	public static String CHECKMARK = "nounproject/checkMark.png";
	public static String CHECK_CROSS = "nounproject/checkCross.png";
	public static String COPY_TAB = "nounproject/copy.png";
	public static String CROSS = "nounproject/cross.png";
	public static String DOWNLOAD = "nounproject/download.png";
	public static String INFO = "nounproject/info.png";
	public static String LINK = "nounproject/link.png";
	public static String REFRESH = "nounproject/refresh.png";
	public static String SEARCH = "nounproject/search.png";
	public static String TABLE = "nounproject/table.png";
	public static String EDIT_TEXT = "nounproject/editText.png";
	public static String TEXT = "nounproject/text.png";
	public static String TOOLS = "nounproject/tools.png";
	public static String TOOLTIP = "nounproject/tooltip.png";
	public static String WARNING = "nounproject/warning.png";
	public static String LOADING = "other/ajax_loader.gif";
	public static String QUERY_ERROR = "nounproject/playSquareError.png";
	public static String EXECUTE_QUERY = "nounproject/playSquare.png";
	public static String LOG_OUT = "nounproject/logOut.png";
	public static String LOG_IN = "nounproject/logIn.png";
	public static String QUESTION_MARK = "nounproject/questionMark.png";
	public static String COMPATIBLE = "nounproject/compatible.png";
	public static String EXTERNAL_LINK = "nounproject/external_link.png";
	public static String LOGO_GITHUB = "logos/github.jpg";
	public static String LOGO_GOOGLE = "logos/google.png";
	public static String LOGO_YAHOO = "logos/yahoo.png";
	public static String LOGO_OPENID = "logos/openid.png";
	
	public static String OTHER_IMAGES_DIR = "images/";
	public static String OTHER_1PX = "other/1px.png";
	
	public static String get(String icon) {
		return icon + "?" + StaticConfig.VERSION;
	}
	
	/**
	 * Get disabled variant of path string 
	 * @param iconPath
	 * @return
	 */
	public static String getDisabled(String iconPath) {
		return appendToBasename(iconPath, "Disabled") + "?" + StaticConfig.VERSION;
	}
	
	/**
	 * Append a string to the basename. google.com/bla/bli.png, becomse google.com/bla/bli_<append>.png
	 * 
	 * @param iconPath
	 * @param append
	 * @return
	 */
	private static String appendToBasename(String iconPath, String append) {
		String extension = "";
		int lastDot = iconPath.lastIndexOf('.');
		if (lastDot > 0) {
			extension = iconPath.substring(lastDot + 1);
			iconPath = iconPath.substring(0, lastDot);
		}
		int lastSlash = iconPath.lastIndexOf("/");
		String path = "";
		String basename = iconPath;
		if (lastSlash > 0) {
			basename = iconPath.substring(lastSlash + 1);
			path = iconPath.substring(0, lastSlash);
		}
		return path + "/" + basename + "_" + append + "." + extension;
	}
}
