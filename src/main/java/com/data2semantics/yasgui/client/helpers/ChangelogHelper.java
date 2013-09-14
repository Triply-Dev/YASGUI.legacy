package com.data2semantics.yasgui.client.helpers;

import com.data2semantics.yasgui.client.View;
import com.data2semantics.yasgui.client.settings.ExternalLinks;
import com.data2semantics.yasgui.client.tab.optionbar.QueryConfigMenu;
import com.data2semantics.yasgui.shared.StaticConfig;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.user.client.Command;

/**
 * This class gets changed for every version, and gets updated whenever we have a new release
 * This class is meant for notifications not belonging in the general 'first usage' help bubble
 */
public class ChangelogHelper {
	private static String CHANGELOG_NOTIFICATION = "YASGUI received an update!<br>"
			+ "View the complete list of changes <a href=\"" + ExternalLinks.YASGUI_CHANGELOG +"\" target=\"_blank\">here</a>.<br>"
			+ "A selection of the new features:"
			+ "<ul>\n" + 
				"<li>YASGUI now works offline as well (for browsers supporting HTML5)</li>\n" + 
				"<li>Version change notifications</li>\n" + 
				"<li>Local installation/integration of YASGUI is now more flexible/easy</li>\n" + 
				"<li>Moved YASGUI to a more stable version</li>\n" + 
			"</ul>";
	private static String RESET_SETTINGS_NOTIFICATION = "Now possible to reset your YASGUI settings";
	private static String SPECIFY_NAMED_GRAPH_NOTIFICATION = "Specify named or default graphs for SPARQL http requests";
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
		showResetSettingsNotification();
		showNamedGraphsNotification();
	}

	private void showNamedGraphsNotification() {
		QueryConfigMenu queryConfigMenu = view.getSelectedTab().getQueryConfigMenu();
		if (queryConfigMenu != null) {
			TooltipProperties tProp = new TooltipProperties();
			tProp.setId(queryConfigMenu.getDOM().getId());
			tProp.setContent(SPECIFY_NAMED_GRAPH_NOTIFICATION);
			tProp.setMy(TooltipProperties.POS_TOP_CENTER);
			tProp.setAt(TooltipProperties.POS_BOTTOM_CENTER);
			Helper.drawTooltip(tProp);
		}
		
		
	}

	private void showResetSettingsNotification() {
		TooltipProperties tProp = new TooltipProperties();
		tProp.setId(view.getElements().getConfigMenu().getDOM().getId());
		tProp.setContent(RESET_SETTINGS_NOTIFICATION);
		tProp.setMy(TooltipProperties.POS_RIGHT_CENTER);
		tProp.setAt(TooltipProperties.POS_LEFT_CENTER);
		Helper.drawTooltip(tProp);
		
	}

	private void showChangelogNotification() {
		TooltipProperties tProp = new TooltipProperties();
		tProp.setId(view.getSelectedTab().getQueryTextArea().getDOM().getId());
		tProp.setContent(CHANGELOG_NOTIFICATION);
		tProp.setMy(TooltipProperties.POS_CENTER);
		tProp.setAt(TooltipProperties.POS_CENTER);
		Helper.drawTooltip(tProp);
	}
	
	
}
