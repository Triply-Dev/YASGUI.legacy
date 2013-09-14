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

public enum Imgs {
	DISCONNECTED("nounproject/disconnected.png"),
	OUTPUT_TABLE("outputFormats/table.png"),
	OUTPUT_TABLE_SIMPLE("outputFormats/simpleTable.png"),
	OUTPUT_RAW("outputFormats/rawResponse.png"),
	CLOSE_TAB_SINGLE("other/close-one.png"),
	CLOSE_TAB_OTHERS("other/close-others.png"),
	CLOSE_TAB_ALL("other/close-all.png"),
	ADD_TAB("nounproject/addPage.png"),
	ADD("nounproject/add.png"),
	BOOKMARK_QUERY("nounproject/bookmarkPage.png"),
	SHOW_BOOKMARKS("nounproject/bookmarks.png"),
	CHECKBOX("nounproject/checkbox.png"),
	CHECKMARK("nounproject/checkMark.png"),
	CHECK_CROSS("nounproject/checkCross.png"),
	COPY_TAB("nounproject/copy.png"),
	CROSS("nounproject/cross.png"),
	DOWNLOAD("nounproject/download.png"),
	DOWNLOAD_ROUND("nounproject/download_round.png"),
	INFO("nounproject/info.png"),
	LINK("nounproject/link.png"),
	REFRESH("nounproject/refresh.png"),
	SEARCH("nounproject/search.png"),
	TABLE("nounproject/table.png"),
	EDIT_TEXT("nounproject/editText.png"),
	TEXT("nounproject/text.png"),
	TOOLS("nounproject/tools.png"),
	TOOLTIP("nounproject/tooltip.png"),
	WARNING("nounproject/warning.png"),
	LOADING("other/ajax_loader.gif"),
	QUERY_ERROR("nounproject/playSquareError.png"),
	EXECUTE_QUERY("nounproject/playSquare.png"),
	LOG_OUT("nounproject/logOut.png"),
	LOG_IN("nounproject/logIn.png"),
	QUESTION_MARK("nounproject/questionMark.png"),
	COMPATIBLE("nounproject/compatible.png"),
	EXTERNAL_LINK("nounproject/external_link.png"),
	INTERNAL_LINK("nounproject/internal_link.png"),
	
	LOGO_GITHUB("logos/github.jpg"),
	LOGO_GOOGLE("logos/google.png"),
	LOGO_YAHOO("logos/yahoo.png"),
	LOGO_OPENID("logos/openid.png"),
	LOGO_DATA2SEMANTICS("logos/data2semantics.png"),
	
	OTHER_IMAGES_DIR("images/"),
	OTHER_1PX("other/1px.png");
	
	private String path;
	private Imgs(String path) {
		this.path = path;
	}
	
	public String get() {
		return path + "?" + getVersion();
	}
	
	public String getUnprocessed() {
		return path;
	}
	public String getDisabled() {
		return appendToBasename(path, "Disabled") + "?" + getVersion();
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
	
	private String getVersion() {
		//replace dot. SmartGWT has some issues when dot is in img url after the '?' 
		//this messes up the 'Over' and 'Disabled' icons
		return StaticConfig.VERSION.replace(".", ""); 
	}
}
