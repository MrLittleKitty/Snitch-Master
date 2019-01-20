package com.gmail.nuclearcat1337.snitch_master.api;

/**
 * Created by Mr_Little_Kitty on 4/4/2017.
 */
public enum SnitchActivity {
	ENTER("entered snitch at"), LOGIN("logged in to snitch at"), LOGOUT("logged out in snitch at");

	private final String text;

	SnitchActivity(String text) {
		this.text = text;
	}

	public String getActivityText() {
		return text;
	}

	public static SnitchActivity FromText(String text) {
		if (text.startsWith("entered")) {
			return ENTER;
		}

		if (text.startsWith("logged in")) {
			return LOGIN;
		}

		return LOGOUT;
	}
}
