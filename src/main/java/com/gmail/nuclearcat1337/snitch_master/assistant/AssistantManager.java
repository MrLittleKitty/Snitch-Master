package com.gmail.nuclearcat1337.snitch_master.assistant;

import com.gmail.nuclearcat1337.snitch_master.SnitchMaster;
import com.gmail.nuclearcat1337.snitch_master.locatableobjectlist.Location;
import com.gmail.nuclearcat1337.snitch_master.snitches.SnitchManager;
import com.gmail.nuclearcat1337.snitch_master.util.Color;
import com.gmail.nuclearcat1337.snitch_master.worldinfo.WorldProvider;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class AssistantManager implements Iterable<AssistantBlock>, Iterator<AssistantBlock> {

    private static final Color COLOR = new Color(139, 0, 0);
    private static final long FLASH_PERIOD = 1000;

    private final EmptyIterable emptyIterable = new EmptyIterable();
    private final SnitchManager snitchManager;
    private final WorldProvider worldProvider;

    private FlashingAssistantBlock assistantBlock;
    private boolean globalRender;
    private AssistantMode mode;
    private Location baseLocation;
    private List<AssistantDirection> offsets;

    public AssistantManager(final SnitchManager snitchManager, final WorldProvider worldProvider) {
        this.snitchManager = snitchManager;
        this.worldProvider = worldProvider;
        this.assistantBlock = null;
        this.baseLocation = null;
        this.globalRender = false;
        this.mode = AssistantMode.PLACEMENT;
        this.offsets = new ArrayList<>();
    }

    public Iterable<AssistantBlock> getBlocksForWorld(final String world) {
        if (assistantBlock == null || !assistantBlock.getWorld().equals(world)) {
            return emptyIterable;
        }
        return this;
    }

    public boolean shouldRender() {
        return globalRender;
    }

    public void invertGlobalRender() {
        this.globalRender = !this.globalRender;
    }

    public void deleteAssistant() {
        assistantBlock = null;
    }

    public void updateBaseLocation() {
        this.baseLocation = baseLocation;
        if (assistantBlock == null) {
            assistantBlock = new FlashingAssistantBlock(baseLocation, COLOR, FLASH_PERIOD);
        }
        applyOffsets();
    }

    public AssistantMode getMode() {
        return this.mode;
    }

    public void setMode(final AssistantMode mode) {
        this.mode = mode;
    }

    private void applyOffsets() {
        int x = baseLocation.getY();
        int y = baseLocation.getY();
        int z = baseLocation.getZ();
        for (final AssistantDirection direction : offsets) {
            x += direction.getXOffset();
            y += direction.getYOffset();
            z += direction.getZOffset();
        }
        if (assistantBlock != null) {
            assistantBlock.setLocation(new Location(x, y, z, baseLocation.getWorld()));
        }
    }

    @Override
    @Nonnull
    public Iterator<AssistantBlock> iterator() {
        if (assistantBlock == null) {
            return emptyIterable.iterator();
        }
        return this;
    }

    @Override
    public boolean hasNext() {
        return true;
    }

    @Override
    public AssistantBlock next() {
        return assistantBlock;
    }

    private class EmptyIterable implements Iterable<AssistantBlock> {

        private final Iterator<AssistantBlock> empty = Collections.emptyIterator();

        @Override
        @Nonnull
        public Iterator<AssistantBlock> iterator() {
            return empty;
        }
    }
}
