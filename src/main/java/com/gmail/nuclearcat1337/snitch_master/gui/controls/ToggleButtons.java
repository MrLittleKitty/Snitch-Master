package com.gmail.nuclearcat1337.snitch_master.gui.controls;

import net.minecraft.client.gui.GuiButton;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ToggleButtons {

    private final List<GuiButton> buttons;
    private int selected = -1;

    public ToggleButtons(final List<GuiButton> buttons, final int defaultSelected) {
        this.buttons = buttons;
        for (final GuiButton button : this.buttons) {
            button.enabled = true;
        }
        updateSelected(defaultSelected);
    }

    public boolean actionPerformed(final GuiButton button) {
        final int index = Collections.binarySearch(buttons, button, new Comparator<GuiButton>() {
            @Override
            public int compare(GuiButton o1, GuiButton o2) {
                return Integer.compare(o1.id, o2.id);
            }
        });

        if (index > -1 && index != selected) {
            updateSelected(index);
            return true;
        }
        return false;
    }

    public GuiButton getSelected() {
        if (selected > -1 && selected < buttons.size()) {
            return buttons.get(selected);
        }
        return null;
    }

    private void updateSelected(final int newSelected) {
        //Enable the current button
        if (selected > -1 && selected < buttons.size()) {
            buttons.get(selected).enabled = true;
        }
        if (newSelected > -1 && newSelected < buttons.size()) {
            buttons.get(newSelected).enabled = false;
        }
        this.selected = newSelected;
    }
}
