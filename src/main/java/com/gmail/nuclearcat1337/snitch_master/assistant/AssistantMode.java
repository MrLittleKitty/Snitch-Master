package com.gmail.nuclearcat1337.snitch_master.assistant;

public enum AssistantMode {
    PLACEMENT("Placement"),
    COVERAGE("Coverage"),
    TEST("Test Block");

    private final String text;

    private AssistantMode(final String text) {
        this.text = text;
    }

    public String getDisplayText() {
        return this.text;
    }

    public AssistantMode getNextMode() {
        final int value = this.ordinal() + 1;
        if (value >= AssistantMode.values().length) {
            return AssistantMode.values()[0];
        }
        return AssistantMode.values()[value];
    }
}
