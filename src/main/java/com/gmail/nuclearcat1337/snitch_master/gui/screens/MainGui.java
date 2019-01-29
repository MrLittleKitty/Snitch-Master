package com.gmail.nuclearcat1337.snitch_master.gui.screens;

import com.gmail.nuclearcat1337.snitch_master.Settings;
import com.gmail.nuclearcat1337.snitch_master.SnitchMaster;
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
    private final SnitchManager manager;
	private final ChatSnitchParser snitchParser;
	private final Settings settings;

	public MainGui(final SnitchManager manager, final ChatSnitchParser snitchParser, final Settings settings) {
		SnitchListRemoveColumn.removedSnitchLists.clear();
		SnitchRemoveColumn.removedSnitches.clear();

		this.manager = manager;
		this.snitchParser = snitchParser;
		this.settings = settings;
	}

	public void initGui() {
		this.buttonList.clear();

		String updateButtonMessage = snitchParser.isUpdatingSnitchList() ? "Cancel Snitch Update" : "Full Snitch Update";

		int xPos = (this.width / 2) - (GuiConstants.LONG_BUTTON_WIDTH / 2);
		int yPos = (this.height / 4) + 8 - (GuiConstants.STANDARD_BUTTON_HEIGHT + GuiConstants.STANDARD_SEPARATION_DISTANCE);

		this.buttonList.add(new GuiButton(0, xPos, yPos, GuiConstants.LONG_BUTTON_WIDTH, GuiConstants.STANDARD_BUTTON_HEIGHT, updateButtonMessage));

		yPos = yPos + GuiConstants.STANDARD_BUTTON_HEIGHT + GuiConstants.STANDARD_SEPARATION_DISTANCE;

		String targetedUpdateButtonMessage = snitchParser.isUpdatingSnitchList() ? "Cancel Snitch Update" : "Targeted Snitch Update";

		this.buttonList.add(new GuiButton(1, xPos, yPos, GuiConstants.LONG_BUTTON_WIDTH, GuiConstants.STANDARD_BUTTON_HEIGHT, targetedUpdateButtonMessage));

		yPos = yPos + GuiConstants.STANDARD_BUTTON_HEIGHT + GuiConstants.STANDARD_SEPARATION_DISTANCE;

		this.buttonList.add(new GuiButton(2, xPos, yPos, GuiConstants.LONG_BUTTON_WIDTH, GuiConstants.STANDARD_BUTTON_HEIGHT, "View Settings"));

		yPos = yPos + GuiConstants.STANDARD_BUTTON_HEIGHT + GuiConstants.STANDARD_SEPARATION_DISTANCE;

		this.buttonList.add(new GuiButton(3, xPos, yPos, "View Snitch Lists"));

		yPos = yPos + GuiConstants.STANDARD_BUTTON_HEIGHT + GuiConstants.STANDARD_SEPARATION_DISTANCE;

		this.buttonList.add(new GuiButton(4, xPos, yPos, "View Snitches"));

		yPos = yPos + GuiConstants.STANDARD_BUTTON_HEIGHT + GuiConstants.STANDARD_SEPARATION_DISTANCE;

		this.buttonList.add(new GuiButton(5, xPos, yPos, "Done"));
	}

	public void actionPerformed(GuiButton button) {
		SnitchListRemoveColumn.removedSnitchLists.clear();
		SnitchRemoveColumn.removedSnitches.clear();
		switch (button.id) {
			case 0: //"Full Snitch Update" or "Cancel Snitch Update"
				if (snitchParser.isUpdatingSnitchList()) {
					snitchParser.resetUpdatingSnitchList(true, true);
				} else {
					snitchParser.updateSnitchList();
				}
				this.mc.displayGuiScreen((GuiScreen) null);
				this.mc.setIngameFocus();
				break;
			case 1:
				if (snitchParser.isUpdatingSnitchList()) {
					snitchParser.resetUpdatingSnitchList(true, true);
					this.mc.displayGuiScreen((GuiScreen) null);
					this.mc.setIngameFocus();
				} else {
					this.mc.displayGuiScreen(new TargetedSnitchUpdateGui(this, snitchParser));
				}
				break;
			case 2: //"View Settings"
				this.mc.displayGuiScreen(new SettingsGui(this, settings));
				break;
			case 3: //"View Snitch Lists"
				this.mc.gameSettings.saveOptions(); //wtf? Why is this here? What does this do?
				this.mc.displayGuiScreen(new SnitchListsTable(this, manager.getSnitchLists(),
                        "All Snitch Lists", true, manager, settings));
				break;
			case 4: //"View Snitches"
				this.mc.displayGuiScreen(new SnitchesTable(this, manager.getSnitches(),
                        "All Snitches", manager, settings, JourneyMapRelay.getInstance()));
				break;
			case 5: //"Done"
				this.mc.displayGuiScreen((GuiScreen) null);
				this.mc.setIngameFocus();
				break;
		}
	}

	@Override
	public boolean doesGuiPauseGame() {
		return false;
	}
}
