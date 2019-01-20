package com.gmail.nuclearcat1337.snitch_master.gui.screens;

import com.gmail.nuclearcat1337.snitch_master.SnitchMaster;
import com.gmail.nuclearcat1337.snitch_master.api.SnitchListQualifier;
import com.gmail.nuclearcat1337.snitch_master.gui.GuiConstants;
import com.gmail.nuclearcat1337.snitch_master.gui.controls.TextBox;
import com.gmail.nuclearcat1337.snitch_master.gui.snitchliststable.SnitchListsTable;
import com.gmail.nuclearcat1337.snitch_master.snitches.SnitchList;
import com.gmail.nuclearcat1337.snitch_master.snitches.SnitchManager;
import com.gmail.nuclearcat1337.snitch_master.util.Color;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import org.lwjgl.input.Keyboard;

import java.io.IOException;

/**
 * Created by Mr_Little_Kitty on 9/18/2016.
 */
public class NewSnitchListGui extends GuiScreen {
	private static final String CREATE_NEW_LIST_STRING = "Create New Snitch List";

	private SnitchMaster snitchMaster;
	private GuiScreen cancelToScreen;

	private TextBox nameBox;
	private TextBox redBox;
	private TextBox greenBox;
	private TextBox blueBox;
	private TextBox qualifierBox;

	private int nameBoxWidth;
	private int qualifierBoxWidth;
	private int rgbBoxWidth;
	private int qualifierStringWidth;
	private int createNewListStringWidth;
	private int buttonWidth;

	public NewSnitchListGui(GuiScreen cancelToScreen, SnitchMaster snitchMaster) {
		this.cancelToScreen = cancelToScreen;
		this.snitchMaster = snitchMaster;
	}

	private static final int MAX_NAME_TEXT_LENGTH = 20;
	private static final int MAX_COLOR_TEXT_LENGTH = 3;

	public void initGui() {
		qualifierBoxWidth = mc.fontRenderer.getStringWidth(SnitchList.MAX_NAME_CHARACTERS + "WWW"); //TODO---Idk this is kind of just an arbitrary width
		nameBoxWidth = qualifierBoxWidth;
		rgbBoxWidth = nameBoxWidth / 3;
		qualifierStringWidth = mc.fontRenderer.getStringWidth("Qualifier");
		createNewListStringWidth = mc.fontRenderer.getStringWidth(CREATE_NEW_LIST_STRING);
		buttonWidth = (qualifierBoxWidth - GuiConstants.STANDARD_SEPARATION_DISTANCE) / 3;

		int yPos = (this.height / 2) - (GuiConstants.STANDARD_BUTTON_HEIGHT * 3) - (GuiConstants.STANDARD_SEPARATION_DISTANCE);
		int xPos = (this.width / 2) - (nameBoxWidth / 2);

		nameBox = new TextBox("", fontRenderer, xPos, yPos, nameBoxWidth, GuiConstants.STANDARD_TEXTBOX_HEIGHT, false, false, MAX_NAME_TEXT_LENGTH);
		nameBox.setFocused(true);

		yPos += (GuiConstants.STANDARD_BUTTON_HEIGHT + GuiConstants.SMALL_SEPARATION_DISTANCE);
		xPos = nameBox.x;

		this.qualifierBox = new TextBox("", fontRenderer, xPos, yPos, qualifierBoxWidth, GuiConstants.STANDARD_TEXTBOX_HEIGHT, false, false, SnitchListQualifier.MAX_QUALIFIER_TEXT_LENGTH);

		yPos += (GuiConstants.STANDARD_BUTTON_HEIGHT + GuiConstants.SMALL_SEPARATION_DISTANCE);

		this.redBox = new TextBox("", fontRenderer, xPos, yPos, rgbBoxWidth, GuiConstants.STANDARD_TEXTBOX_HEIGHT, true, false, MAX_COLOR_TEXT_LENGTH);
		redBox.setClamp(0, 255);

		yPos += (GuiConstants.STANDARD_BUTTON_HEIGHT + GuiConstants.SMALL_SEPARATION_DISTANCE);

		this.greenBox = new TextBox("", fontRenderer, xPos, yPos, rgbBoxWidth, GuiConstants.STANDARD_TEXTBOX_HEIGHT, true, false, MAX_COLOR_TEXT_LENGTH);
		greenBox.setClamp(0, 255);

		yPos += (GuiConstants.STANDARD_BUTTON_HEIGHT + GuiConstants.SMALL_SEPARATION_DISTANCE);

		this.blueBox = new TextBox("", fontRenderer, xPos, yPos, rgbBoxWidth, GuiConstants.STANDARD_TEXTBOX_HEIGHT, true, false, MAX_COLOR_TEXT_LENGTH);
		blueBox.setClamp(0, 255);

		this.buttonList.clear();

		xPos += (blueBox.width + GuiConstants.SMALL_SEPARATION_DISTANCE);

		this.buttonList.add(new GuiButton(1, xPos, yPos, buttonWidth, GuiConstants.STANDARD_BUTTON_HEIGHT, "Cancel"));

		xPos += (buttonWidth + GuiConstants.SMALL_SEPARATION_DISTANCE);

		this.buttonList.add(new GuiButton(2, xPos, yPos, buttonWidth, GuiConstants.STANDARD_BUTTON_HEIGHT, "Create"));

		super.initGui();
	}

	public void updateScreen() {
		nameBox.updateCursorCounter();
		qualifierBox.updateCursorCounter();
		redBox.updateCursorCounter();
		greenBox.updateCursorCounter();
		blueBox.updateCursorCounter();
		super.updateScreen();
	}

	public void keyTyped(char par1, int par2) throws IOException {
		if (nameBox.isFocused()) {
			if (par2 == Keyboard.KEY_TAB) {
				qualifierBox.setFocused(true);
				nameBox.setFocused(false);
			}
			nameBox.textboxKeyTyped(par1, par2);
		} else if (qualifierBox.isFocused()) {
			if (par2 == Keyboard.KEY_TAB) {
				redBox.setFocused(true);
				qualifierBox.setFocused(false);
			}
			qualifierBox.textboxKeyTyped(par1, par2);
		} else if (redBox.isFocused()) {
			if (par2 == Keyboard.KEY_TAB) {
				greenBox.setFocused(true);
				redBox.setFocused(false);
			}
			redBox.textboxKeyTyped(par1, par2);
		} else if (greenBox.isFocused()) {
			if (par2 == Keyboard.KEY_TAB) {
				blueBox.setFocused(true);
				greenBox.setFocused(false);
			}
			greenBox.textboxKeyTyped(par1, par2);
		} else if (blueBox.isFocused()) {
			if (par2 == Keyboard.KEY_TAB) {
				nameBox.setFocused(true);
				blueBox.setFocused(false);
			}
			blueBox.textboxKeyTyped(par1, par2);
		}
		super.keyTyped(par1, par2);
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		this.drawDefaultBackground();

		int constYValue = (GuiConstants.STANDARD_TEXTBOX_HEIGHT / 2) - mc.fontRenderer.FONT_HEIGHT / 2;
		int constXValue = GuiConstants.SMALL_SEPARATION_DISTANCE + qualifierStringWidth;

		mc.fontRenderer.drawString("Blue", blueBox.x - constXValue, blueBox.y + constYValue, 16777215);
		mc.fontRenderer.drawString("Green", greenBox.x - constXValue, greenBox.y + constYValue, 16777215);
		mc.fontRenderer.drawString("Red", redBox.x - constXValue, redBox.y + constYValue, 16777215);
		mc.fontRenderer.drawString("Qualifier", qualifierBox.x - constXValue, qualifierBox.y + constYValue, 16777215);
		mc.fontRenderer.drawString("Name", nameBox.x - constXValue, nameBox.y + constYValue, 16777215);

		int yPos = nameBox.y - GuiConstants.STANDARD_SEPARATION_DISTANCE - mc.fontRenderer.FONT_HEIGHT;
		int xPos = nameBox.x + (nameBoxWidth / 2) - (createNewListStringWidth / 2);

		mc.fontRenderer.drawString(CREATE_NEW_LIST_STRING, xPos, yPos, 16777215);

		this.nameBox.drawTextBox();
		this.qualifierBox.drawTextBox();
		this.redBox.drawTextBox();
		this.greenBox.drawTextBox();
		this.blueBox.drawTextBox();
		super.drawScreen(mouseX, mouseY, partialTicks);
	}

	public void mouseClicked(int one, int two, int three) throws IOException {
		this.nameBox.mouseClicked(one, two, three);
		this.qualifierBox.mouseClicked(one, two, three);
		this.redBox.mouseClicked(one, two, three);
		this.blueBox.mouseClicked(one, two, three);
		this.greenBox.mouseClicked(one, two, three);
		super.mouseClicked(one, two, three);
	}

	public void actionPerformed(GuiButton button) {
		switch (button.id) {
			case 1:
				Minecraft.getMinecraft().displayGuiScreen(cancelToScreen);
				break;
			case 2:
				String name = nameBox.getText();
				if (name == null) {
					break;
				}
				Integer red = redBox.clamp();
				if (red == null) {
					break;
				}
				Integer green = greenBox.clamp();
				if (green == null) {
					break;
				}
				Integer blue = blueBox.clamp();
				if (blue == null) {
					break;
				}
				String qualifier = qualifierBox.getText();
				if (qualifier == null || !SnitchListQualifier.isSyntaxValid(qualifier)) {
					break;
				}

				SnitchManager manager = snitchMaster.getManager();
				if (manager.doesListWithNameExist(name)) {
					break;
				}

				//Creating a new snitch list automatically triggers a save of all snitch lists
				manager.createSnitchList(name, new SnitchListQualifier(qualifier), true, new Color(red, green, blue));

				this.mc.displayGuiScreen(new SnitchListsTable(null, manager.getSnitchLists(), "All Snitch Lists", true, snitchMaster));
				break;
		}
	}

	@Override
	public boolean doesGuiPauseGame() {
		return false;
	}
}
