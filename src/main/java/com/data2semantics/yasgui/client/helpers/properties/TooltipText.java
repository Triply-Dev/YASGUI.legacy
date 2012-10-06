package com.data2semantics.yasgui.client.helpers.properties;

public class TooltipText {
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
}
