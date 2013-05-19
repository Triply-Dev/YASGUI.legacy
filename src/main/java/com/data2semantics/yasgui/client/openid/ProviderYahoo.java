package com.data2semantics.yasgui.client.openid;

import com.data2semantics.yasgui.shared.StaticConfig;

public class ProviderYahoo implements OpenIdProvider{
	private static String NAME = "yahoo";
	private static String URL = "https://me.yahoo.com/";
	private static boolean REQUIRE_USERNAME = false;
	private static int MAX_ICON_HEIGHT = 50;
	public String getName() {
		return NAME;
	}

	public String getUrl() {
		return URL;
	}

	public boolean requireUsername() {
		return REQUIRE_USERNAME;
	}

	public String getImageLocation() {
		return StaticConfig.OPEN_ID_PROVIDER_IMG_PATH + NAME + ".png";
	}
	
	public int getMaxIconHeight() {
		return MAX_ICON_HEIGHT;
	}

}
