/*******************************************************************************
 * Copyright (c)  2012 Laurens Rietveld
 * 
 *  Permission is hereby granted, free of charge, to any person
 *  obtaining a copy of this software and associated documentation
 *  files (the "Software"), to deal in the Software without
 *  restriction, including without limitation the rights to use,
 *  copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the
 *  Software is furnished to do so, subject to the following
 *  conditions:
 * 
 *  The above copyright notice and this permission notice shall be
 *  included in all copies or substantial portions of the Software.
 * 
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 *  EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
 *  OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 *  NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
 *  HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 *  WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 *  FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
 *  OTHER DEALINGS IN THE SOFTWARE.
 ******************************************************************************/
package com.data2semantics.yasgui.client.helpers.properties;

public class TooltipText {
	public static final String GITHUB_ICON = "Found bugs? Miss any features? Submit an issue on GitHub";
	public static String TOOLTIP_BUTTON = "Click this button to show these hints again";
	public static String CONFIG_MENU = "<ul style=\"margin: 1px;\">" +
			"<li>Update prefixes (e.g. after adding prefix to www.prefix.cc)</li>" +
			"<li>Update list of endpoints from CKAN</li>" +
			"</ul>";
	public static String TAB_SELECTION = "Double click to rename. Right click for tab options";
	public static String ENDPOINT_SEARCH_ICON = "Search for endpoints";
	public static String QUERY_PREFIXES_AUTOCOMPLETE = "Start typing the PREFIX definition to get an autocompletion list of prefixes.<br>Prefix missing? Add your prefix to <a href=\"http://prefix.cc/\" target=\"_blank\">prefix.cc</a>, and refresh autocompletion list (via config menu)";
	public static String QUERY_KEYBOARD_SHORTCUTS = "Available keyboard shortcuts:" +
			"<ul>\n" + 
				"<li><strong>CTRL-D</strong>: Delete line</li>\n" + 
				"<li><strong>CTRL-ALT-down</strong>: Copy line below</li>\n" + 
				"<li><strong>CTRL-ALT-up</strong>: Copy line up</li>\n" + 
				"<li><strong>CTRL-/</strong>: Comment line(s)</li>\n" + 
			"</ul>";
	public static String QUERY_CONFIG_MENU = "Advanced options which you normally wont need, including:" +
			"<ul style=\"margin: 1px;\">\n" + 
			"<li>Changing request method (POST or GET)</li>\n" + 
			"<li>Adding additional query arguments</li>\n" + 
			"<li>Changing requested content (XML or JSON)</li>\n" + 
		"</ul>";
}
