package com.data2semantics.yasgui.client.helpers;

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

import com.data2semantics.yasgui.client.View;
import com.data2semantics.yasgui.client.settings.ExternalLinks;
import com.data2semantics.yasgui.shared.StaticConfig;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.user.client.Command;

/**
 * This class gets changed for every version, and gets updated whenever we have a new release
 * This class is meant for notifications not belonging in the general 'first usage' help bubble
 */
public class ChangelogHelper {
	private View view;
	
	public ChangelogHelper(View view) {
		this.view = view;
		int versionId = LocalStorageHelper.getVersionId();
		if (versionId != 0 && versionId < StaticConfig.VERSION_ID) {
			//if there is a version change, AND this isnt a new user, draw changelog notifications
			Scheduler.get().scheduleDeferred(new Command() {
				public void execute() {
					draw();
				}
			});
		}
	}
	
	public void draw() {
		showChangelogNotification();
		showAutoFormatShortcutNotification();
	}

	
	private void showAutoFormatShortcutNotification() {
		TooltipProperties tProp = new TooltipProperties();
		tProp.setId(view.getSelectedTab().getQueryTextArea().getDOM().getId());
		String shortcut = "'" + (JsMethods.isMac()?"Command":"Ctrl") + "-Space'";
		tProp.setTitle("Autocomplete properties and classes!");
		tProp.setText("Go to a possible property or class position in your query and press " + shortcut);
		tProp.setMy(TooltipProperties.POS_CENTER);
		tProp.setAt(TooltipProperties.POS_LEFT_CENTER);
		tProp.setYOffset(50);
		tProp.setXOffset(180);
		Helper.drawTooltip(tProp);
	}

	private void showChangelogNotification() {
		TooltipProperties tProp = new TooltipProperties();
		tProp.setId(view.getSelectedTab().getQueryTextArea().getDOM().getId());
		tProp.setTitle("YASGUI received an update!");
		tProp.setText(""
				+ "View the complete list of changes <a href=\"" + ExternalLinks.YASGUI_CHANGELOG +"\" target=\"_blank\">here</a>.<br>"
				+ "A selection of the new features:"
				+ "<ul>\n" + 
					"<li>Autocompletion for properties and classes</li>\n" + 
					"<li>Persistent query results between sessions</li>\n" + 
				"</ul>");
		tProp.setMy(TooltipProperties.POS_CENTER);
		tProp.setAt(TooltipProperties.POS_CENTER);
		Helper.drawTooltip(tProp);
	}
	
	
}
