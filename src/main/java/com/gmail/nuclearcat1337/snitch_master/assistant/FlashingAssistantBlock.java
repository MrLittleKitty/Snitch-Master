package com.gmail.nuclearcat1337.snitch_master.assistant;

import com.gmail.nuclearcat1337.snitch_master.Settings;
import com.gmail.nuclearcat1337.snitch_master.locatableobjectlist.Location;
import com.gmail.nuclearcat1337.snitch_master.snitches.Snitch;
import com.gmail.nuclearcat1337.snitch_master.util.Color;
import com.gmail.nuclearcat1337.snitch_master.util.GeneralUtils;

import javax.annotation.Nonnull;

class FlashingAssistantBlock extends AssistantBlock {

    private final long flashPeriod;
    private final Color color;

    private Location location;
    private long nextFlashTime;

    public FlashingAssistantBlock(final Location location, final Color color, final long flashPeriod) {
        this.location = location;
        this.color = color;
        this.flashPeriod = flashPeriod;
        this.nextFlashTime = 0;
    }

    @Override
    public String getWorld() {
        return this.location.getWorld();
    }

    @Override
    public int compareTo(final Location other) {
        return GeneralUtils.compareLocations(this.location, other);
    }

    @Override
    public Location getLocation() {
        return this.location;
    }

    public void setLocation(final Location location) {
        this.location = location;
    }

    @Override
    public int getBoxRadius() {
        return Settings.SNITCH_RADIUS;
    }

    @Override
    public boolean shouldRender(final long currentTime) {
        return currentTime >= nextFlashTime;
    }

    @Override
    public void wasRendered(final long currentTime) {
        nextFlashTime = currentTime + this.flashPeriod;
    }

    @Override
    public Color getColor() {
        return this.color;
    }

    @Override
    public int compareTo(@Nonnull final AssistantBlock other) {
        return compareTo(other.getLocation());
    }
}
