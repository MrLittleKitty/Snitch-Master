package com.gmail.nuclearcat1337.snitch_master.gui.screens;

import com.gmail.nuclearcat1337.snitch_master.Settings;
import com.gmail.nuclearcat1337.snitch_master.SnitchMaster;
import com.gmail.nuclearcat1337.snitch_master.gui.GuiConstants;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;

public class SettingsGui extends GuiScreen {
	private final GuiScreen backToScreen;
	private final Settings settings;

	private GuiButton chatSpamButton;
	private GuiButton quietTimeButton;
	private GuiButton renderTextButton;
	private GuiButton manualModeButton;

	public SettingsGui(GuiScreen backToScreen) {
		this.backToScreen = backToScreen;
		//This is a hack so that this class can be used with the Forge "config" button
		this.settings = SnitchMaster.instance.getSettings();
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
		Boolean state = (Boolean) settings.getValue(Settings.MANUAL_MODE_KEY);
		state = state ? Boolean.FALSE : Boolean.TRUE; //Invert the state
		settings.setValue(Settings.MANUAL_MODE_KEY, state);
	}

	private void updateManualModeButton() {
		Boolean state = (Boolean) settings.getValue(Settings.MANUAL_MODE_KEY);
		String text = state ? "Manual Mode On" : "Manual Mode Off";
		manualModeButton.displayString = text;
	}

	private void nextRenderTextState() {
		Boolean state = (Boolean) settings.getValue(Settings.RENDER_TEXT_KEY);
		state = state ? Boolean.FALSE : Boolean.TRUE; //Invert the state
		settings.setValue(Settings.RENDER_TEXT_KEY, state);
	}

	private void updateRenderTextButton() {
		Boolean state = (Boolean) settings.getValue(Settings.RENDER_TEXT_KEY);
		String text = state ? "Render Text On" : "Render Text Off";
		renderTextButton.displayString = text;
	}

	private void nextChatSpamState() {
		Settings.ChatSpamState chatSpamState = (Settings.ChatSpamState) settings.getValue(Settings.CHAT_SPAM_KEY);
		if (chatSpamState == Settings.ChatSpamState.ON) {
			chatSpamState = Settings.ChatSpamState.OFF;
		} else if (chatSpamState == Settings.ChatSpamState.OFF) {
			chatSpamState = Settings.ChatSpamState.PAGENUMBERS;
		} else if (chatSpamState == Settings.ChatSpamState.PAGENUMBERS) {
			chatSpamState = Settings.ChatSpamState.ON;
		}
		settings.setValue(Settings.CHAT_SPAM_KEY, chatSpamState);
	}

	private void updateChatSpamButton() {
		String jaListSpamText = "Updating Snitches Spam: ";
		Settings.ChatSpamState chatSpamState = (Settings.ChatSpamState) settings.getValue(Settings.CHAT_SPAM_KEY);
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
