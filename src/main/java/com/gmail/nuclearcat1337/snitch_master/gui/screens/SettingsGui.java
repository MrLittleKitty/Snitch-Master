package com.gmail.nuclearcat1337.snitch_master.gui.screens;

import com.gmail.nuclearcat1337.snitch_master.Settings;
import com.gmail.nuclearcat1337.snitch_master.SnitchMaster;
import com.gmail.nuclearcat1337.snitch_master.gui.GuiConstants;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;

/**
 * Created by Mr_Little_Kitty on 12/29/2016.
 */
public class SettingsGui extends GuiScreen {
	private final GuiScreen backToScreen;
	private final Settings settings;

	private GuiButton chatSpamButton;
	private GuiButton quietTimeButton;
	private GuiButton renderTextButton;
	private GuiButton manualModeButton;

	public SettingsGui(final GuiScreen backToScreen, final Settings settings) {
		this.backToScreen = backToScreen;
		//This is a hack so that this class can be used with the Forge "config" button
		this.settings = settings;
	}

	public void initGui() {
		this.buttonList.clear();

		int xPos = (this.width / 2) - (GuiConstants.LONG_BUTTON_WIDTH / 2);
		int yPos = (this.height / 2) - (((GuiConstants.STANDARD_BUTTON_HEIGHT * 3) + (GuiConstants.STANDARD_SEPARATION_DISTANCE * 2)) / 2);

		//Set the drawXPos variable for drawing 2 half sized buttons
		int drawXPos = xPos;
		int halfWidth = (GuiConstants.LONG_BUTTON_WIDTH / 2) - (GuiConstants.STANDARD_SEPARATION_DISTANCE / 2);

		renderTextButton = new GuiButton(1, drawXPos, yPos, halfWidth, GuiConstants.STANDARD_BUTTON_HEIGHT, "");
		updateRenderTextButton();
		this.buttonList.add(renderTextButton);

		//Increment it for drawing the second button
		drawXPos += (halfWidth + GuiConstants.STANDARD_SEPARATION_DISTANCE);
		manualModeButton = new GuiButton(2, drawXPos, yPos, halfWidth, GuiConstants.STANDARD_BUTTON_HEIGHT, "");
		updateManualModeButton();
		this.buttonList.add(manualModeButton);

		yPos = yPos + GuiConstants.STANDARD_BUTTON_HEIGHT + GuiConstants.STANDARD_SEPARATION_DISTANCE;

		quietTimeButton = new GuiButton(3, xPos, yPos, GuiConstants.LONG_BUTTON_WIDTH, GuiConstants.STANDARD_BUTTON_HEIGHT, "Quiet Time Config");
		this.buttonList.add(quietTimeButton);

		yPos = yPos + GuiConstants.STANDARD_BUTTON_HEIGHT + GuiConstants.STANDARD_SEPARATION_DISTANCE;

		chatSpamButton = new GuiButton(4, xPos, yPos, GuiConstants.LONG_BUTTON_WIDTH, GuiConstants.STANDARD_BUTTON_HEIGHT, "");
		updateChatSpamButton();
		this.buttonList.add(chatSpamButton);

		yPos = yPos + GuiConstants.STANDARD_BUTTON_HEIGHT + GuiConstants.STANDARD_SEPARATION_DISTANCE;

		this.buttonList.add(new GuiButton(0, xPos, yPos, GuiConstants.LONG_BUTTON_WIDTH, GuiConstants.STANDARD_BUTTON_HEIGHT, "Done"));
	}

	public void actionPerformed(GuiButton button) {
		switch (button.id) {
			case 0: //"Done"
				this.mc.displayGuiScreen(backToScreen);
				break;
			case 1: //"Render Text"
				nextRenderTextState();
				updateRenderTextButton();
				settings.saveSettings();
				break;
			case 2: //Manual Mode
				nextManualModeState();
				updateManualModeButton();
				settings.saveSettings();
				break;
			case 3: //"Quiet Time"
				this.mc.displayGuiScreen(new QuietTimeGui(this, settings));
				break;
			case 4: //"Updating Snitches Spam: "
				nextChatSpamState();
				updateChatSpamButton();
				settings.saveSettings();
				break;
		}
	}

	private void nextManualModeState() {
		settings.setManualMode(!settings.getManualMode());
	}

	private void updateManualModeButton() {
		manualModeButton.displayString = settings.getManualMode() ? "Manual Mode On" : "Manual Mode Off";
	}

	private void nextRenderTextState() {
		settings.setRenderText(!settings.getRenderText());
	}

	private void updateRenderTextButton() {
		renderTextButton.displayString = settings.getRenderText() ? "Render Text On" : "Render Text Off";
	}

	private void nextChatSpamState() {
		Settings.ChatSpamState chatSpamState = settings.getChatSpamState();
		if (chatSpamState == Settings.ChatSpamState.ON) {
			chatSpamState = Settings.ChatSpamState.OFF;
		} else if (chatSpamState == Settings.ChatSpamState.OFF) {
			chatSpamState = Settings.ChatSpamState.PAGENUMBERS;
		} else if (chatSpamState == Settings.ChatSpamState.PAGENUMBERS) {
			chatSpamState = Settings.ChatSpamState.ON;
		}
		settings.setChatSpamState(chatSpamState);
	}

	private void updateChatSpamButton() {
		String jaListSpamText = "Updating Snitches Spam: ";
		final Settings.ChatSpamState chatSpamState = settings.getChatSpamState();
		if (chatSpamState == Settings.ChatSpamState.ON) {
			jaListSpamText += "On";
		} else if (chatSpamState == Settings.ChatSpamState.OFF) {
			jaListSpamText += "Off";
		} else if (chatSpamState == Settings.ChatSpamState.PAGENUMBERS) {
			jaListSpamText += "Page Numbers";
		}
		chatSpamButton.displayString = jaListSpamText;
	}

	@Override
	public boolean doesGuiPauseGame() {
		return false;
	}
}
