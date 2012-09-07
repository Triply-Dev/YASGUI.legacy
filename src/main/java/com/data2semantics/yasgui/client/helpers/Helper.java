package com.data2semantics.yasgui.client.helpers;

import java.util.ArrayList;
import com.data2semantics.yasgui.client.settings.Settings;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.user.client.Cookies;
import com.google.gwt.user.client.Window;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.widgets.Label;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;

public class Helper {
	private static String COOKIE_SETTINGS = "yasgui_settings";

	public static String implode(ArrayList<String> arrayList, String glue) {
		String result = "";
		for (String stringItem : arrayList) {
			if (result.length() > 0) {
				result += glue;
			}
			result += stringItem;
		}
		return result;
	}

	public static void storeSettingsInCookie(Settings settings) {
		Cookies.removeCookie(COOKIE_SETTINGS);
		Cookies.setCookie(COOKIE_SETTINGS, settings.toString());
	}

	public static Settings getSettingsFromCookie() {
		Settings settings = new Settings();
		String jsonString = Cookies.getCookie(COOKIE_SETTINGS);
		if (jsonString != null && jsonString.length() > 0) {
			JSONObject jsonObject = JSONParser.parseStrict(jsonString).isObject();
			if (jsonObject == null) {
				// Something went wrong. Just use original 'bare' settings
				// objects
			} else {
				settings = new Settings(jsonObject);
			}
		}
		return settings;
	}
	//You could also use an HTMLFlow element
	public static Label getLink(String message, ClickHandler handler) {
	   Label link = new Label();
	   link = new Label(message);
	   link.addStyleName("clickable");
	   link.setHeight100();
	   link.setWidth100();
	   link.setAlign(Alignment.CENTER);

	   //Set the width to the length of the text.
	   link.setWidth(message.length()*6);

	   link.addClickHandler(handler);
	   return link;

	}
	public static Label getLinkNewWindow(String message, final String url) {
		return getLink(message, new ClickHandler(){

			@Override
			public void onClick(ClickEvent event) {
				Window.open(url, "_blank", null);
			}});
	}
}
