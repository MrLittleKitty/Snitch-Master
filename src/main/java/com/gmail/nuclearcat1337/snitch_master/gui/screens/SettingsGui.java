package com.gmail.nuclearcat1337.snitch_master.gui.screens;

import com.gmail.nuclearcat1337.snitch_master.Settings;
import com.gmail.nuclearcat1337.snitch_master.gui.GuiConstants;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;

/**
 * Created by Mr_Little_Kitty on 12/29/2016.
 */
public class SettingsGui extends GuiScreen {
    private static final int SNITCH_TEXT_ID = 0;
    private static final int SNITCH_RENDER_ID = 1;
    private static final int MANUAL_MODE_ID = 2;
    private static final int QUIET_TIME_ID = 3;
    private static final int CHAT_SPAM_ID = 4;
    private static final int DONE_ID = 5;

    private final GuiScreen backToScreen;
    private final Settings settings;

    private GuiButton chatSpamButton;
    private GuiButton quietTimeButton;
    private GuiButton snitchTextButton;
    private GuiButton snitchRenderButton;
    private GuiButton manualModeButton;

    public SettingsGui(final GuiScreen backToScreen, final Settings settings) {
        this.backToScreen = backToScreen;
        //This is a hack so that this class can be used with the Forge "config" button
        this.settings = settings;
    }

    public void initGui() {
        this.buttonList.clear();

        final int separationDistance = GuiConstants.STANDARD_SEPARATION_DISTANCE;
        final int buttonHeight = GuiConstants.STANDARD_BUTTON_HEIGHT;
        final int halfWidth = (GuiConstants.LONG_BUTTON_WIDTH - separationDistance) / 2;
        final int firstColumnX = (this.width / 2) - (GuiConstants.MEDIUM_BUTTON_WIDTH) - (separationDistance / 2);
        final int secondColumnX = firstColumnX + GuiConstants.MEDIUM_BUTTON_WIDTH + separationDistance;

        int yPos = (this.height / 2) - (GuiConstants.STANDARD_SEPARATION_DISTANCE / 2) - (GuiConstants.STANDARD_BUTTON_HEIGHT * 2)
                - GuiConstants.STANDARD_SEPARATION_DISTANCE;

        snitchRenderButton = new GuiButton(SNITCH_RENDER_ID, firstColumnX, yPos, halfWidth, buttonHeight,
                getButtonText(SNITCH_RENDER_ID));

        snitchTextButton = new GuiButton(SNITCH_TEXT_ID, secondColumnX, yPos, halfWidth, buttonHeight,
                getButtonText(SNITCH_TEXT_ID));

        yPos = yPos + buttonHeight + separationDistance;

        manualModeButton = new GuiButton(MANUAL_MODE_ID, firstColumnX, yPos, halfWidth, buttonHeight,
                getButtonText(MANUAL_MODE_ID));

        yPos = yPos + buttonHeight + separationDistance;

        quietTimeButton = new GuiButton(QUIET_TIME_ID, firstColumnX, yPos, halfWidth, buttonHeight,
                getButtonText(QUIET_TIME_ID));

        chatSpamButton = new GuiButton(CHAT_SPAM_ID, secondColumnX, yPos, halfWidth, buttonHeight,
                getButtonText(CHAT_SPAM_ID));

        yPos = yPos + GuiConstants.STANDARD_BUTTON_HEIGHT + GuiConstants.STANDARD_SEPARATION_DISTANCE;

        final GuiButton doneButton = new GuiButton(DONE_ID, firstColumnX, yPos, GuiConstants.LONG_BUTTON_WIDTH,
                buttonHeight, getButtonText(DONE_ID));

        this.buttonList.add(snitchRenderButton);
        this.buttonList.add(snitchTextButton);
        this.buttonList.add(manualModeButton);
        this.buttonList.add(quietTimeButton);
        this.buttonList.add(chatSpamButton);
        this.buttonList.add(doneButton);
    }

    public void actionPerformed(GuiButton button) {
        switch (button.id) {
            case DONE_ID: //"Done"
                this.mc.displayGuiScreen(backToScreen);
                break;
            case SNITCH_TEXT_ID: //"Render Text"
                settings.setRenderText(!settings.getRenderText());
                this.snitchTextButton.displayString = getButtonText(SNITCH_TEXT_ID);
                settings.saveSettings();
                break;
            case MANUAL_MODE_ID: //Manual Mode
                settings.setManualMode(!settings.getManualMode());
                this.manualModeButton.displayString = getButtonText(MANUAL_MODE_ID);
                settings.saveSettings();
                break;
            case QUIET_TIME_ID: //"Quiet Time"
                this.mc.displayGuiScreen(new QuietTimeGui(this, settings));
                break;
            case CHAT_SPAM_ID: //"Updating Snitches Spam: "
                settings.setChatSpamState(settings.getChatSpamState().getNextState());
                this.chatSpamButton.displayString = getButtonText(CHAT_SPAM_ID);
                settings.saveSettings();
                break;
        }
    }

    private String getButtonText(final int id) {
        switch (id) {
            case SNITCH_RENDER_ID:
                return "Snitch Render: " + (settings.getGlobalRender() ? "On" : "Off");
            case SNITCH_TEXT_ID:
                return "Snitch Text: " + (settings.getRenderText() ? "On" : "Off");
            case MANUAL_MODE_ID:
                return "Manual Mode: " + (settings.getManualMode() ? "On" : "Off");
            case QUIET_TIME_ID:
                return "Quiet Time Config";
            case CHAT_SPAM_ID:
                return "Snitch Spam: " + settings.getChatSpamState().getDisplayText();
            case DONE_ID:
                return "Done";
        }
        return "";
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }
}
