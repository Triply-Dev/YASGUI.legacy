package com.data2semantics.yasgui.client.openid;

import com.data2semantics.yasgui.shared.StaticConfig;

public class ProviderGoogle implements OpenIdProvider{
	private static String NAME = "google";
	private static String URL = "https://www.google.com/accounts/o8/id";
	private static boolean REQUIRE_USERNAME = false;
	private int MAX_ICON_HEIGHT = 60;
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
