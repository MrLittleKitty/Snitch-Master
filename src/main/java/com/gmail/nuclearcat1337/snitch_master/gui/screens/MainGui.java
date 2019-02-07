package com.gmail.nuclearcat1337.snitch_master.gui.screens;

import com.gmail.nuclearcat1337.snitch_master.Settings;
import com.gmail.nuclearcat1337.snitch_master.SnitchMaster;
import com.gmail.nuclearcat1337.snitch_master.assistant.AssistantManager;
import com.gmail.nuclearcat1337.snitch_master.gui.GuiConstants;
import com.gmail.nuclearcat1337.snitch_master.gui.snitchliststable.SnitchListRemoveColumn;
import com.gmail.nuclearcat1337.snitch_master.gui.snitchliststable.SnitchListsTable;
import com.gmail.nuclearcat1337.snitch_master.gui.snitchtable.SnitchRemoveColumn;
import com.gmail.nuclearcat1337.snitch_master.gui.snitchtable.SnitchesTable;
import com.gmail.nuclearcat1337.snitch_master.handlers.ChatSnitchParser;
import com.gmail.nuclearcat1337.snitch_master.journeymap.JourneyMapRelay;
import com.gmail.nuclearcat1337.snitch_master.snitches.SnitchManager;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;

/**
 * Created by Mr_Little_Kitty on 9/11/2016.
 */
public class MainGui extends GuiScreen {
    private static final int SNITCH_UPDATE = 0;
    private static final int LOAD_SNITCHES = 1;
    private static final int SNITCHES = 2;
    private static final int SNITCH_LISTS = 3;
    private static final int SETTINGS = 4;
    private static final int ASSISTANT = 5;
    private static final int DONE = 6;

    private final SnitchManager manager;
    private final ChatSnitchParser snitchParser;
    private final Settings settings;
    private final AssistantManager assistantManager;

    private final AssistantGUI assistantGUI;

    public MainGui(final SnitchManager manager, final ChatSnitchParser snitchParser, final Settings settings,
                   final AssistantManager assistantManager) {
        SnitchListRemoveColumn.removedSnitchLists.clear();
        SnitchRemoveColumn.removedSnitches.clear();

        this.manager = manager;
        this.snitchParser = snitchParser;
        this.settings = settings;
        this.assistantManager = assistantManager;
        assistantGUI = new AssistantGUI(assistantManager);
    }

    public void initGui() {
        this.buttonList.clear();

        final String updateButtonMessage = snitchParser.isUpdatingSnitchList() ? "Cancel Update" : "Snitch Update";

        int xPos = (this.width / 2) - (GuiConstants.MEDIUM_BUTTON_WIDTH) - (GuiConstants.STANDARD_SEPARATION_DISTANCE / 2);
        int yPos = (this.height / 2) - (GuiConstants.STANDARD_SEPARATION_DISTANCE / 2) - (GuiConstants.STANDARD_BUTTON_HEIGHT * 2)
                - GuiConstants.STANDARD_SEPARATION_DISTANCE;

        this.buttonList.add(new GuiButton(SNITCH_UPDATE, xPos, yPos, GuiConstants.MEDIUM_BUTTON_WIDTH, GuiConstants.STANDARD_BUTTON_HEIGHT, updateButtonMessage));

        xPos = xPos + GuiConstants.MEDIUM_BUTTON_WIDTH + GuiConstants.STANDARD_SEPARATION_DISTANCE;

        //this.buttonList.add(new GuiButton(LOAD_SNITCHES, xPos, yPos, GuiConstants.MEDIUM_BUTTON_WIDTH, GuiConstants.STANDARD_BUTTON_HEIGHT, "Load Snitches"));

        yPos = yPos + GuiConstants.STANDARD_BUTTON_HEIGHT + GuiConstants.STANDARD_SEPARATION_DISTANCE;
        xPos = (this.width / 2) - (GuiConstants.MEDIUM_BUTTON_WIDTH) - (GuiConstants.STANDARD_SEPARATION_DISTANCE / 2);

        this.buttonList.add(new GuiButton(SNITCHES, xPos, yPos, GuiConstants.MEDIUM_BUTTON_WIDTH, GuiConstants.STANDARD_BUTTON_HEIGHT, "Snitches"));

        xPos = xPos + GuiConstants.MEDIUM_BUTTON_WIDTH + GuiConstants.STANDARD_SEPARATION_DISTANCE;

        this.buttonList.add(new GuiButton(SNITCH_LISTS, xPos, yPos, GuiConstants.MEDIUM_BUTTON_WIDTH, GuiConstants.STANDARD_BUTTON_HEIGHT, "Snitch Lists"));

        yPos = yPos + GuiConstants.STANDARD_BUTTON_HEIGHT + GuiConstants.STANDARD_SEPARATION_DISTANCE;
        xPos = (this.width / 2) - (GuiConstants.MEDIUM_BUTTON_WIDTH) - (GuiConstants.STANDARD_SEPARATION_DISTANCE / 2);

        this.buttonList.add(new GuiButton(SETTINGS, xPos, yPos, GuiConstants.MEDIUM_BUTTON_WIDTH, GuiConstants.STANDARD_BUTTON_HEIGHT, "Settings"));

        xPos = xPos + GuiConstants.MEDIUM_BUTTON_WIDTH + GuiConstants.STANDARD_SEPARATION_DISTANCE;

        this.buttonList.add(new GuiButton(ASSISTANT, xPos, yPos, GuiConstants.MEDIUM_BUTTON_WIDTH, GuiConstants.STANDARD_BUTTON_HEIGHT, "Assistant"));

        yPos = yPos + GuiConstants.STANDARD_BUTTON_HEIGHT + GuiConstants.STANDARD_SEPARATION_DISTANCE;
        xPos = (this.width / 2) - (GuiConstants.LONG_BUTTON_WIDTH / 2);

        this.buttonList.add(new GuiButton(DONE, xPos, yPos, "Done"));

        xPos = (this.width / 2) + (GuiConstants.LONG_BUTTON_WIDTH / 2) + (GuiConstants.STANDARD_SEPARATION_DISTANCE * 2);
        yPos = (this.height / 2) - (GuiConstants.STANDARD_SEPARATION_DISTANCE / 2) - (GuiConstants.STANDARD_BUTTON_HEIGHT * 2)
                - GuiConstants.STANDARD_SEPARATION_DISTANCE;

        assistantGUI.init(xPos, yPos);
        if (assistantManager.shouldRender()) {
            assistantGUI.addButtons(this.buttonList);
        }
    }

    public void actionPerformed(final GuiButton button) {
        SnitchListRemoveColumn.removedSnitchLists.clear();
        SnitchRemoveColumn.removedSnitches.clear();
        switch (button.id) {
            case SNITCH_UPDATE: //"Full Snitch Update" or "Cancel Snitch Update"
                if (snitchParser.isUpdatingSnitchList()) {
                    snitchParser.resetUpdatingSnitchList(true, true);
                } else {
                    snitchParser.updateSnitchList();
                }
                this.mc.displayGuiScreen((GuiScreen) null);
                this.mc.setIngameFocus();
                break;
            case LOAD_SNITCHES:

                break;
            case SETTINGS: //"View Settings"
                this.mc.displayGuiScreen(new SettingsGui(this, settings));
                break;
            case SNITCH_LISTS: //"View Snitch Lists"
                this.mc.gameSettings.saveOptions(); //wtf? Why is this here? What does this do?
                this.mc.displayGuiScreen(new SnitchListsTable(this, manager.getSnitchLists(),
                        "All Snitch Lists", true, manager, settings));
                break;
            case SNITCHES: //"View Snitches"
                this.mc.displayGuiScreen(new SnitchesTable(this, manager.getSnitches(),
                        "All Snitches", manager, settings, JourneyMapRelay.getInstance()));
                break;
            case ASSISTANT: //"Assistant"
                assistantManager.invertGlobalRender();
                if (assistantManager.shouldRender()) {
                    assistantGUI.addButtons(this.buttonList);
                } else {
                    assistantGUI.removeButtons(this.buttonList);
                    assistantManager.deleteAssistant();
                }
                break;
            case DONE: //"Done"
                this.mc.displayGuiScreen((GuiScreen) null);
                this.mc.setIngameFocus();
                break;

            default:
                assistantGUI.actionPerformed(button);
                break;
        }
    }

    @Override
    public void drawScreen(final int mouseX, final int mouseY, final float partialTicks) {
        super.drawScreen(mouseX, mouseY, partialTicks);
        if (assistantManager.shouldRender()) {
            assistantGUI.drawScreen(mouseX, mouseY, partialTicks);
        }
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }
}
