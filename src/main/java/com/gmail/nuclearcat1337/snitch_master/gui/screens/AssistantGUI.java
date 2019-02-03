package com.gmail.nuclearcat1337.snitch_master.gui.screens;

import com.gmail.nuclearcat1337.snitch_master.gui.GuiConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;

import java.util.List;

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
    private Mode currentMode;

    public void init(final int topLeftX, final int topLeftY) {
        currentMode = Mode.PLACEMENT;

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

    }

    private String getButtonText(final int id) {
        switch (id) {
            case MODE_ID:
                return currentMode.getDisplayText();
            case UPDATE_ID:
                return "Update";
            case N_ID:
                if (currentMode == Mode.PLACEMENT) {
                    return "N";
                } else {
                    return "NW";
                }
            case S_ID:
                if (currentMode == Mode.PLACEMENT) {
                    return "S";
                } else {
                    return "SW";
                }
            case E_ID:
                if (currentMode == Mode.PLACEMENT) {
                    return "E";
                } else {
                    return "NE";
                }
            case W_ID:
                if (currentMode == Mode.PLACEMENT) {
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

    private enum Mode {
        PLACEMENT("Placement"),
        COVERAGE("Coverage");

        private final String text;

        private Mode(final String text) {
            this.text = text;
        }

        public String getDisplayText() {
            return this.text;
        }
    }
}
