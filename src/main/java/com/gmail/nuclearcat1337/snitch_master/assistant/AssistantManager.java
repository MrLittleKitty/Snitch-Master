package com.gmail.nuclearcat1337.snitch_master.assistant;

import com.gmail.nuclearcat1337.snitch_master.locatableobjectlist.Location;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.Iterator;

public class AssistantManager implements Iterable<AssistantBlock>, Iterator<AssistantBlock> {

    private final EmptyIterable emptyIterable = new EmptyIterable();

    private FlashingAssistantBlock assistantBlock;
    private boolean globalRender;

    public AssistantManager() {
        this.assistantBlock = null;
        this.globalRender = false;
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

    public void setAssistantLocation(final Location location) {
        if (assistantBlock != null) {
            assistantBlock.setLocation(location);
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
