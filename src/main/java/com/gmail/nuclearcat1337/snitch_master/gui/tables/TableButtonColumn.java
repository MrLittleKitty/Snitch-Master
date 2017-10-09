package com.gmail.nuclearcat1337.snitch_master.gui.tables;

import com.gmail.nuclearcat1337.snitch_master.gui.GuiConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;

import java.util.List;

public class TableButtonColumn<T> implements TableColumn<T> {
	private static Minecraft mc;

	private final String columnName;
	private final String buttonText;
	private final int buttonWidth;
	private final OnButtonClick<T> onClick;

	public TableButtonColumn(String columnName, String buttonText, int buttonWidth, OnButtonClick<T> onClick) {
		mc = Minecraft.getMinecraft();
		this.columnName = columnName;
		this.buttonText = buttonText;
		this.buttonWidth = buttonWidth;
		this.onClick = onClick;
	}

	@Override
	public GuiButton[] prepareEntry(T item) {
		GuiButton[] buttons = new GuiButton[1];
		buttons[0] = new GuiButton(0, 0, 0, buttonWidth, GuiConstants.STANDARD_BUTTON_HEIGHT, buttonText);
		return buttons;
	}

	@Override
	public String getColumnName() {
		return columnName;
	}

	@Override
	public boolean doBoundsCheck() {
		return true;
	}

	@Override
	public void clicked(T item, boolean leftClick, int xPos, int yPos, GuiButton[] buttons, GuiScreen parentScreen, int slotIndex) {
		if (!leftClick) {
			return;
		}
		if (buttons[0].mousePressed(mc, xPos, yPos)) {
			onClick.onClick(item, buttons[0], parentScreen);
		}
	}

	@Override
	public void released(T list, int xPos, int yPos, GuiButton[] buttons, GuiScreen parentScreen, int slotIndex) {
		buttons[0].mouseReleased(xPos, yPos);
	}

	@Override
	public void draw(T list, int xPosition, int yPosition, int columnWidth, int slotHeight, GuiButton[] buttons, int slotIndex, int mouseX, int mouseY) {
		yPosition = yPosition + ((slotHeight - GuiConstants.STANDARD_BUTTON_HEIGHT) / 2);
		int xPos = xPosition + (columnWidth / 2) - (buttonWidth / 2);

		buttons[0].y = yPosition;
		buttons[0].x = xPos;

		buttons[0].drawButton(mc, mouseX, mouseY, 0);
	}

	@Override
	public int getDrawWidth(T list) {
		return buttonWidth;
	}

	@Override
	public List<String> hover(T item, int xPos, int yPos) {
		return null;
	}

	@Override
	public boolean canSort() {
		return false;
	}

	@Override
	public int compare(T o1, T o2) {
		return 0;
	}

	public interface OnButtonClick<T> {
		void onClick(T item, GuiButton button, GuiScreen parent);
	}
}
