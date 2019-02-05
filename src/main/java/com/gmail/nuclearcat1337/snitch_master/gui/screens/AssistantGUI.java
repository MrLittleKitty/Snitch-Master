package com.gmail.nuclearcat1337.snitch_master.gui.screens;

import com.gmail.nuclearcat1337.snitch_master.assistant.AssistantDirection;
import com.gmail.nuclearcat1337.snitch_master.assistant.AssistantManager;
import com.gmail.nuclearcat1337.snitch_master.assistant.AssistantMode;
import com.gmail.nuclearcat1337.snitch_master.gui.GuiConstants;
import com.gmail.nuclearcat1337.snitch_master.gui.controls.ToggleButtons;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static com.gmail.nuclearcat1337.snitch_master.assistant.AssistantDirection.*;

public class AssistantGUI {

    private static Minecraft mc = Minecraft.getMinecraft();
    private static final int MODE_ID = 7;
    private static final int UPDATE_ID = 8;
    private static final int N_ID = 9;
    private static final int S_ID = 10;
    private static final int E_ID = 11;
    private static final int W_ID = 12;
    private static final int ABOVE_ID = 13;
    private static final int BELOW_ID = 14;

    private final AssistantManager manager;

    private List<ToggleButtons> toggleControls;
    private GuiButton modeButton;
    private GuiButton updateButton;
    private GuiButton northButton;
    private GuiButton southButton;
    private GuiButton eastButton;
    private GuiButton westButton;
    private GuiButton aboveButton;
    private GuiButton belowButton;

    private String text;
    private int textX;
    private int textY;

    public AssistantGUI(final AssistantManager manager) {
        this.manager = manager;
        this.toggleControls = new ArrayList<>(2);
    }

    public void init(final int topLeftX, final int topLeftY) {
        final int separationDistance = GuiConstants.STANDARD_SEPARATION_DISTANCE;
        final int buttonHeight = GuiConstants.STANDARD_BUTTON_HEIGHT;
        final int buttonWidth = GuiConstants.MEDIUM_BUTTON_WIDTH - 5;

        text = "Assistant Mode";
        final int textWidth = mc.fontRenderer.getStringWidth(text);
        final int textHeight = mc.fontRenderer.FONT_HEIGHT;

        int x = topLeftX;
        int y = topLeftY;

        textX = x + (buttonWidth / 2) - (textWidth / 2);
        textY = y - (textHeight + 2);

        modeButton = new GuiButton(MODE_ID, x, y, buttonWidth, buttonHeight,
                getButtonText(MODE_ID));

        y = y + buttonHeight + separationDistance;

        updateButton = new GuiButton(UPDATE_ID, x, y, buttonWidth, buttonHeight,
                getButtonText(UPDATE_ID));

        y = y + buttonHeight + separationDistance;

        final int directionButtonWidth = (buttonWidth - (separationDistance * 3)) / 4;

        northButton = new GuiButton(N_ID, x, y, directionButtonWidth, buttonHeight, getButtonText(N_ID));
        x = x + directionButtonWidth + separationDistance;

        southButton = new GuiButton(S_ID, x, y, directionButtonWidth, buttonHeight, getButtonText(S_ID));
        x = x + directionButtonWidth + separationDistance;

        eastButton = new GuiButton(E_ID, x, y, directionButtonWidth, buttonHeight, getButtonText(E_ID));
        x = x + directionButtonWidth + separationDistance;

        westButton = new GuiButton(W_ID, x, y, directionButtonWidth, buttonHeight, getButtonText(W_ID));

        y = y + buttonHeight + separationDistance;
        x = topLeftX;

        final int aboveBelowButtonWidth = (buttonWidth - separationDistance) / 2;

        aboveButton = new GuiButton(ABOVE_ID, x, y, aboveBelowButtonWidth, buttonHeight,
                getButtonText(ABOVE_ID));

        x = x + aboveBelowButtonWidth + separationDistance;

        belowButton = new GuiButton(BELOW_ID, x, y, aboveBelowButtonWidth, buttonHeight,
                getButtonText(BELOW_ID));

        updateToggle();
    }

    public void addButtons(final List<GuiButton> buttons) {
        buttons.add(modeButton);
        buttons.add(updateButton);
        buttons.add(aboveButton);
        buttons.add(belowButton);
        buttons.add(northButton);
        buttons.add(southButton);
        buttons.add(eastButton);
        buttons.add(westButton);
    }

    public void removeButtons(final List<GuiButton> buttons) {
        buttons.remove(modeButton);
        buttons.remove(updateButton);
        buttons.remove(aboveButton);
        buttons.remove(belowButton);
        buttons.remove(northButton);
        buttons.remove(southButton);
        buttons.remove(eastButton);
        buttons.remove(westButton);
    }

    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        mc.fontRenderer.drawString(text, textX, textY, 16777215);
    }

    public void actionPerformed(final GuiButton button) {
        switch (button.id) {
            case MODE_ID: {
                manager.setMode(manager.getMode().getNextMode());
                updateButtonText();
                updateToggle();
                break;
            }
            case UPDATE_ID: {
                manager.updateBaseLocation();
                break;
            }
            default: {
                for (int i = 0; i < toggleControls.size(); i++) {
                    final ToggleButtons toggle = toggleControls.get(i);
                    if (toggle.actionPerformed(button)) {
                        manager.setOffset(i, getDirection(toggle.getSelected().id));
                        updateToggle();
                        break;
                    }
                }
                break;
            }
        }
    }

    private void updateToggle() {
        toggleControls.clear();
        switch (manager.getMode()) {
            case PLACEMENT: {
                final List<GuiButton> buttons = new ArrayList<>(6);
                buttons.add(northButton);
                buttons.add(southButton);
                buttons.add(eastButton);
                buttons.add(westButton);
                buttons.add(aboveButton);
                buttons.add(belowButton);

                final int startingIndex = getIndex(buttons, manager.getOffset(0));
                toggleControls.add(new ToggleButtons(buttons, startingIndex));
                break;
            }
            case COVERAGE: {
                final List<GuiButton> directionButtons = new ArrayList<>(4);
                directionButtons.add(northButton);
                directionButtons.add(southButton);
                directionButtons.add(eastButton);
                directionButtons.add(westButton);
                final List<GuiButton> aboveBelow = new ArrayList<>(2);
                aboveBelow.add(aboveButton);
                aboveBelow.add(belowButton);

                final int startingIndex = getIndex(directionButtons, manager.getOffset(0));
                final int startingIndex2 = getIndex(aboveBelow, manager.getOffset(1));
                toggleControls.add(new ToggleButtons(directionButtons, startingIndex));
                toggleControls.add(new ToggleButtons(aboveBelow, startingIndex2));
                break;
            }
        }
    }

    private void updateButtonText() {
        modeButton.displayString = getButtonText(MODE_ID);
        updateButton.displayString = getButtonText(UPDATE_ID);
        northButton.displayString = getButtonText(N_ID);
        southButton.displayString = getButtonText(S_ID);
        eastButton.displayString = getButtonText(E_ID);
        westButton.displayString = getButtonText(W_ID);
        aboveButton.displayString = getButtonText(ABOVE_ID);
        belowButton.displayString = getButtonText(BELOW_ID);
    }

    private AssistantDirection getDirection(final int id) {
        switch (id) {
            case N_ID:
                if (manager.getMode() == AssistantMode.PLACEMENT) {
                    return NORTH;
                } else {
                    return NORTHWEST;
                }
            case S_ID:
                if (manager.getMode() == AssistantMode.PLACEMENT) {
                    return SOUTH;
                } else {
                    return SOUTHWEST;
                }
            case E_ID:
                if (manager.getMode() == AssistantMode.PLACEMENT) {
                    return EAST;
                } else {
                    return NORTHEAST;
                }
            case W_ID:
                if (manager.getMode() == AssistantMode.PLACEMENT) {
                    return WEST;
                } else {
                    return SOUTHEAST;
                }
            case ABOVE_ID:
                return ABOVE;
            case BELOW_ID:
                return BELOW;
        }
        return null;
    }

    private String getButtonText(final int id) {
        switch (id) {
            case MODE_ID:
                return manager.getMode().getDisplayText();
            case UPDATE_ID:
                return "Update";
            case N_ID:
                if (manager.getMode() == AssistantMode.PLACEMENT) {
                    return "N";
                } else {
                    return "NW";
                }
            case S_ID:
                if (manager.getMode() == AssistantMode.PLACEMENT) {
                    return "S";
                } else {
                    return "SW";
                }
            case E_ID:
                if (manager.getMode() == AssistantMode.PLACEMENT) {
                    return "E";
                } else {
                    return "NE";
                }
            case W_ID:
                if (manager.getMode() == AssistantMode.PLACEMENT) {
                    return "W";
                } else {
                    return "SE";
                }
            case ABOVE_ID:
                return "Above";
            case BELOW_ID:
                return "Below";
        }
        return "Undefined";
    }

    private int mapDirectionToId(final AssistantDirection direction) {
        switch (direction) {
            case NORTH:
            case NORTHWEST:
                return N_ID;
            case SOUTH:
            case SOUTHWEST:
                return S_ID;
            case EAST:
            case NORTHEAST:
                return E_ID;
            case WEST:
            case SOUTHEAST:
                return W_ID;
            case ABOVE:
                return ABOVE_ID;
            case BELOW:
                return BELOW_ID;
        }
        return -1;
    }

    private int getIndex(final List<GuiButton> buttons, final AssistantDirection direction) {
        final int id = mapDirectionToId(direction);
        for (int i = 0; i < buttons.size(); i++) {
            if (buttons.get(i).id == id)
                return i;
        }
        return -1;
    }
}
