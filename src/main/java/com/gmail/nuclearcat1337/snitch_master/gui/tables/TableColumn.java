package com.gmail.nuclearcat1337.snitch_master.gui.tables;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;

import java.util.Comparator;
import java.util.List;

public interface TableColumn<T> extends Comparator<T> {
	GuiButton[] prepareEntry(T item);

	//Must be less than or equal to column width (this is also the header)
	String getColumnName();

	boolean doBoundsCheck();

	void clicked(T item, boolean leftClick, int xPos, int yPos, GuiButton[] buttons, GuiScreen parentScreen, int slotIndex);

	void released(T item, int xPos, int yPos, GuiButton[] buttons, GuiScreen parentScreen, int slotIndex);

	void draw(T item, int xPos, int yPos, int columnWidth, int slotHeight, GuiButton[] buttons, int slotIndex, int mouseX, int mouseY);

	int getDrawWidth(T item);

	//Returns a list of strings to draw as hover text
	List<String> hover(T item, int xPos, int yPos);

	boolean canSort();
}
