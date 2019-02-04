package com.gmail.nuclearcat1337.snitch_master.assistant;

import com.gmail.nuclearcat1337.snitch_master.SnitchMaster;
import com.gmail.nuclearcat1337.snitch_master.locatableobjectlist.Location;
import com.gmail.nuclearcat1337.snitch_master.snitches.Snitch;
import com.gmail.nuclearcat1337.snitch_master.snitches.SnitchManager;
import com.gmail.nuclearcat1337.snitch_master.util.Color;
import com.gmail.nuclearcat1337.snitch_master.worldinfo.WorldProvider;
import net.minecraft.client.Minecraft;
import net.minecraft.util.math.BlockPos;

import javax.annotation.Nonnull;
import java.util.*;

public class AssistantManager {

    private static final Color COLOR = new Color(139, 0, 0);
    private static final long FLASH_ON = 1500;
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
        final BlockPos pos = Minecraft.getMinecraft().player.getPosition();
        final List<Snitch> intersectingSnitches = snitchManager.getIntersectingSnitches(
                new Location(pos.getX(), pos.getY(), pos.getZ(), worldProvider.getCurrentWorld()));
        if (intersectingSnitches != null && !intersectingSnitches.isEmpty()) {
            final Snitch closest = intersectingSnitches.get(0);
            this.baseLocation = closest.getLocation();
            if (assistantBlock == null) {
                assistantBlock = new FlashingAssistantBlock(baseLocation, COLOR, FLASH_ON, FLASH_OFF);
                iterable = Collections.singletonList(assistantBlock);
            }
            applyOffsets();
        } else {
            deleteAssistant();
        }
    }

    public void setOffset(final int offsetPosition, final AssistantDirection offset) {
        offsets.put(offsetPosition, offset);
        applyOffsets();
    }

    public AssistantMode getMode() {
        return this.mode;
    }

    public void setMode(final AssistantMode mode) {
        this.mode = mode;
    }

    private void applyOffsets() {
        if (baseLocation != null && assistantBlock != null) {
            int x = baseLocation.getX();
            int y = baseLocation.getY();
            int z = baseLocation.getZ();
            for (final AssistantDirection direction : offsets.values()) {
                x += direction.getXOffset();
                y += direction.getYOffset();
                z += direction.getZOffset();
            }
            assistantBlock.setLocation(new Location(x, y, z, baseLocation.getWorld()));
        }
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
