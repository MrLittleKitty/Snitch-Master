package com.gmail.nuclearcat1337.snitch_master.gui.screens;

import com.gmail.nuclearcat1337.snitch_master.SnitchMaster;
import com.gmail.nuclearcat1337.snitch_master.gui.GuiConstants;
import com.gmail.nuclearcat1337.snitch_master.gui.snitchliststable.SnitchListRemoveColumn;
import com.gmail.nuclearcat1337.snitch_master.gui.snitchliststable.SnitchListsTable;
import com.gmail.nuclearcat1337.snitch_master.gui.snitchtable.SnitchRemoveColumn;
import com.gmail.nuclearcat1337.snitch_master.gui.snitchtable.SnitchesTable;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;

public class MainGui extends GuiScreen {
	private SnitchMaster snitchMaster;

	public MainGui(SnitchMaster snitchMaster) {
		this.snitchMaster = snitchMaster;
		SnitchListRemoveColumn.removedSnitchLists.clear();
		SnitchRemoveColumn.removedSnitches.clear();
	}

	public void initGui() {
		this.buttonList.clear();

		String updateButtonMessage = snitchMaster.getChatSnitchParser().isUpdatingSnitchList() ? "Cancel Snitch Update" : "Full Snitch Update";

		int xPos = (this.width / 2) - (GuiConstants.LONG_BUTTON_WIDTH / 2);
		int yPos = (this.height / 4) + 8 - (GuiConstants.STANDARD_BUTTON_HEIGHT + GuiConstants.STANDARD_SEPARATION_DISTANCE);

		this.buttonList.add(new GuiButton(0, xPos, yPos, GuiConstants.LONG_BUTTON_WIDTH, GuiConstants.STANDARD_BUTTON_HEIGHT, updateButtonMessage));

		yPos = yPos + GuiConstants.STANDARD_BUTTON_HEIGHT + GuiConstants.STANDARD_SEPARATION_DISTANCE;

		String targetedUpdateButtonMessage = snitchMaster.getChatSnitchParser().isUpdatingSnitchList() ? "Cancel Snitch Update" : "Targeted Snitch Update";

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
				if (snitchMaster.getChatSnitchParser().isUpdatingSnitchList()) {
					snitchMaster.getChatSnitchParser().resetUpdatingSnitchList(true, true);
				} else {
					snitchMaster.getChatSnitchParser().updateSnitchList();
				}
				this.mc.displayGuiScreen((GuiScreen) null);
				this.mc.setIngameFocus();
				break;
			case 1:
				if (snitchMaster.getChatSnitchParser().isUpdatingSnitchList()) {
					snitchMaster.getChatSnitchParser().resetUpdatingSnitchList(true, true);
					this.mc.displayGuiScreen((GuiScreen) null);
					this.mc.setIngameFocus();
				} else {
					this.mc.displayGuiScreen(new TargetedSnitchUpdateGui(this, snitchMaster.getChatSnitchParser()));
				}
				break;
			case 2: //"View Settings"
				this.mc.displayGuiScreen(new SettingsGui(this));
				break;
			case 3: //"View Snitch Lists"
				this.mc.gameSettings.saveOptions(); //wtf? Why is this here? What does this do?
				this.mc.displayGuiScreen(new SnitchListsTable(this, snitchMaster.getManager().getSnitchLists(), "All Snitch Lists", true, snitchMaster));
				break;
			case 4: //"View Snitches"
				this.mc.displayGuiScreen(new SnitchesTable(this, snitchMaster.getManager().getSnitches(), "All Snitches", snitchMaster));
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
