package com.gmail.nuclearcat1337.snitch_master.api;

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
