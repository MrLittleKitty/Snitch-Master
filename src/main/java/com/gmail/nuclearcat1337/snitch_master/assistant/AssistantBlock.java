package com.gmail.nuclearcat1337.snitch_master.assistant;

import com.gmail.nuclearcat1337.snitch_master.locatableobjectlist.LocatableObject;
import com.gmail.nuclearcat1337.snitch_master.locatableobjectlist.Location;
import com.gmail.nuclearcat1337.snitch_master.util.Color;

public abstract class AssistantBlock extends LocatableObject<AssistantBlock> {

    public abstract Location getLocation();

    public abstract int getBoxRadius();

    public abstract boolean shouldRender(final long currentTime);

    public abstract void wasRendered(final long currentTime);

    public abstract Color getColor();
}
