package com.gmail.nuclearcat1337.snitch_master.assistant;

import com.gmail.nuclearcat1337.snitch_master.locatableobjectlist.Location;
import com.gmail.nuclearcat1337.snitch_master.snitches.Snitch;
import com.gmail.nuclearcat1337.snitch_master.snitches.SnitchManager;
import com.gmail.nuclearcat1337.snitch_master.util.Color;
import com.gmail.nuclearcat1337.snitch_master.util.GeneralUtils;
import com.gmail.nuclearcat1337.snitch_master.worldinfo.WorldProvider;


import javax.annotation.Nonnull;
import java.util.*;

public class AssistantManager {

    private static final Color COLOR = new Color(138, 43, 226);
    private static final long FLASH_ON = 1100;
    private static final long FLASH_OFF = 500;

    private final EmptyIterable emptyIterable = new EmptyIterable();
    private final SnitchManager snitchManager;
    private final WorldProvider worldProvider;

    private FlashingAssistantBlock assistantBlock;
    private Iterable<AssistantBlock> iterable;
    private boolean globalRender;
    private AssistantMode mode;
    private Location baseLocation;
    private final Map<Integer, AssistantDirection> offsets;

    public AssistantManager(final SnitchManager snitchManager, final WorldProvider worldProvider) {
        this.snitchManager = snitchManager;
        this.worldProvider = worldProvider;

        this.assistantBlock = null;
        this.iterable = null;
        this.baseLocation = null;
        this.globalRender = false;

        this.mode = AssistantMode.PLACEMENT;
        this.offsets = new HashMap<>();
        this.offsets.put(0, AssistantDirection.NORTH);
        this.offsets.put(1, AssistantDirection.BELOW_PLACEMENT);
    }

    public Iterable<AssistantBlock> getBlocksForWorld(final String world) {
        if (assistantBlock == null || !assistantBlock.getWorld().equals(world)) {
            return emptyIterable;
        }
        return iterable;
    }

    public boolean shouldRender() {
        return globalRender;
    }

    public void invertGlobalRender() {
        this.globalRender = !this.globalRender;
    }

    public void deleteAssistant() {
        assistantBlock = null;
        iterable = null;
    }

    public void updateBaseLocation() {
        final Location pos = GeneralUtils.getPlayerLocation(worldProvider.getCurrentWorld());
        if (mode == AssistantMode.PLACEMENT) {
            final List<Snitch> intersectingSnitches = snitchManager.getIntersectingSnitches(
                    new Location(pos.getX(), pos.getY(), pos.getZ(), worldProvider.getCurrentWorld()));
            if (intersectingSnitches != null && !intersectingSnitches.isEmpty()) {
                final Snitch closest = intersectingSnitches.get(0);
                setBaseLocation(closest.getLocation());
            }
        } else if (mode == AssistantMode.COVERAGE || mode == AssistantMode.TEST) {
            setBaseLocation(new Location(pos.getX(), pos.getY(), pos.getZ(), worldProvider.getCurrentWorld()));
        }
    }

    private void setBaseLocation(final Location location) {
        this.baseLocation = location;
        if (assistantBlock == null) {
            assistantBlock = new FlashingAssistantBlock(baseLocation, COLOR, FLASH_ON, FLASH_OFF);
            iterable = Collections.singletonList(assistantBlock);
        }
        applyOffsets();
    }

    public void setOffset(final int offsetPosition, final AssistantDirection offset) {
        offsets.put(offsetPosition, offset);
        applyOffsets();
    }

    public AssistantDirection getOffset(final int offsetPosition) {
        return offsets.get(offsetPosition);
    }

    public AssistantMode getMode() {
        return this.mode;
    }

    public void setMode(final AssistantMode mode) {
        deleteAssistant();
        this.mode = mode;
        if (this.mode == AssistantMode.PLACEMENT) {
            setOffset(0, AssistantDirection.NORTH);
        } else if (this.mode == AssistantMode.COVERAGE) {
            setOffset(0, AssistantDirection.NORTHWEST);
            setOffset(1, AssistantDirection.BELOW_COVERAGE);
        }
    }

    private void applyOffsets() {
        if (baseLocation != null && assistantBlock != null) {
            int x = baseLocation.getX();
            int y = baseLocation.getY();
            int z = baseLocation.getZ();
            if (mode == AssistantMode.PLACEMENT || mode == AssistantMode.COVERAGE) {
                for (final AssistantDirection direction : getOffsets()) {
                    x += direction.getXOffset();
                    y += direction.getYOffset();
                    z += direction.getZOffset();
                }
            }
            assistantBlock.setLocation(new Location(x, y, z, baseLocation.getWorld()));
        }
    }

    private Iterable<AssistantDirection> getOffsets() {
        if (mode == AssistantMode.PLACEMENT) {
            return Collections.singletonList(offsets.get(0));
        } else if (mode == AssistantMode.COVERAGE) {
            return offsets.values();
        }
        return Collections.emptyList();
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
