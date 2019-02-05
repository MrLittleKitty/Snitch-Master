package com.gmail.nuclearcat1337.snitch_master.assistant;

import com.gmail.nuclearcat1337.snitch_master.Settings;

public enum AssistantDirection {

    NORTH(0, 0, -1 * ((Settings.SNITCH_RADIUS * 2) + 1)),
    SOUTH(0, 0, ((Settings.SNITCH_RADIUS * 2) + 1)),
    EAST(((Settings.SNITCH_RADIUS * 2) + 1), 0, 0),
    WEST(-1 * ((Settings.SNITCH_RADIUS * 2) + 1), 0, 0),
    NORTHEAST(Settings.SNITCH_RADIUS, 0, -1 * Settings.SNITCH_RADIUS),
    SOUTHEAST(Settings.SNITCH_RADIUS, 0, Settings.SNITCH_RADIUS),
    NORTHWEST(-1 * Settings.SNITCH_RADIUS, 0, -1 * Settings.SNITCH_RADIUS),
    SOUTHWEST(-1 * Settings.SNITCH_RADIUS, 0, Settings.SNITCH_RADIUS),
    ABOVE_PLACEMENT(0, ((Settings.SNITCH_RADIUS * 2) + 1), 0),
    BELOW_PLACEMENT(0, -1 * ((Settings.SNITCH_RADIUS * 2) + 1), 0),
    ABOVE_COVERAGE(0, Settings.SNITCH_RADIUS, 0),
    BELOW_COVERAGE(0, -1 * Settings.SNITCH_RADIUS, 0);


    private final int xOffset;
    private final int yOffset;
    private final int zOffset;

    private AssistantDirection(final int xOffset, final int yOffset, final int zOffset) {
        this.xOffset = xOffset;
        this.yOffset = yOffset;
        this.zOffset = zOffset;
    }

    public int getXOffset() {
        return this.xOffset;
    }

    public int getYOffset() {
        return this.yOffset;
    }

    public int getZOffset() {
        return this.zOffset;
    }
}
