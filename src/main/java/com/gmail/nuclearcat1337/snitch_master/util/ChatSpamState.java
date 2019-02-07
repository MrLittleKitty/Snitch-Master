package com.gmail.nuclearcat1337.snitch_master.util;

public enum ChatSpamState {
    ON("On"),
    OFF("Off"),
    PAGE_NUMBERS("Pg. #");

    private final String displayText;

    private ChatSpamState(final String displayText) {
        this.displayText = displayText;
    }

    public ChatSpamState getNextState() {
        final int value = this.ordinal() + 1;
        if (value >= ChatSpamState.values().length) {
            return ChatSpamState.values()[0];
        }
        return ChatSpamState.values()[value];
    }

    public String getDisplayText() {
        return this.displayText;
    }
}
