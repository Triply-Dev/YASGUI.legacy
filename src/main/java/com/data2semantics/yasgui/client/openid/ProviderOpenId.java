package com.data2semantics.yasgui.client.openid;

import com.data2semantics.yasgui.shared.StaticConfig;

public class ProviderOpenId implements OpenIdProvider{
	private static String NAME = "openid";
	private static String URL = null;
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
