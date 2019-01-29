package com.gmail.nuclearcat1337.snitch_master.gui.snitchliststable;

import com.gmail.nuclearcat1337.snitch_master.SnitchMaster;
import com.gmail.nuclearcat1337.snitch_master.gui.GuiConstants;
import com.gmail.nuclearcat1337.snitch_master.gui.tables.TableColumn;
import com.gmail.nuclearcat1337.snitch_master.snitches.SnitchList;
import com.gmail.nuclearcat1337.snitch_master.snitches.SnitchManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;

import java.util.HashSet;
import java.util.List;

/**
 * Created by Mr_Little_Kitty on 1/3/2017.
 */
public class SnitchListRemoveColumn implements TableColumn<SnitchList> {
	private static Minecraft mc;

	private static final String BUTTON_TEXT = "x";

	private final SnitchManager manager;
	private final int buttonWidth;
	public static final HashSet<String> removedSnitchLists = new HashSet<>();

	public SnitchListRemoveColumn(final SnitchManager manager) {
		mc = Minecraft.getMinecraft();
		buttonWidth = mc.fontRenderer.getStringWidth(BUTTON_TEXT + "---");
		this.manager = manager;
	}

	@Override
	public GuiButton[] prepareEntry(SnitchList item) {
		GuiButton[] buttons = new GuiButton[1];
		buttons[0] = new GuiButton(0, 0, 0, buttonWidth, GuiConstants.STANDARD_BUTTON_HEIGHT, BUTTON_TEXT);
		return buttons;
	}

	@Override
	public String getColumnName() {
		return "Remove";
	}

	@Override
	public boolean doBoundsCheck() {
		return true;
	}

	@Override
	public void clicked(SnitchList item, boolean leftClick, int xPos, int yPos, GuiButton[] buttons, GuiScreen parentScreen, int slotIndex) {
		if (!leftClick || removedSnitchLists.contains(item.getName())) {
			return;
		}

		if (buttons[0].mousePressed(mc, xPos, yPos)) {
			manager.removeSnitchList(item.getName());

			//Deleting a snitch list automatically triggers a save
			removedSnitchLists.add(item.getName());
		}
	}

	@Override
	public void released(SnitchList list, int xPos, int yPos, GuiButton[] buttons, GuiScreen parentScreen, int slotIndex) {
		if (removedSnitchLists.contains(list.getName())) {
			return;
		}
		buttons[0].mouseReleased(xPos, yPos);
	}

	@Override
	public void draw(SnitchList list, int xPosition, int yPosition, int columnWidth, int slotHeight, GuiButton[] buttons, int slotIndex, int mouseX, int mouseY) {
		if (removedSnitchLists.contains(list.getName())) {
			return;
		}

		yPosition = yPosition + ((slotHeight - GuiConstants.STANDARD_BUTTON_HEIGHT) / 2);
		int xPos = xPosition + (columnWidth / 2) - (buttonWidth / 2);

		buttons[0].y = yPosition;
		buttons[0].x = xPos;

		buttons[0].drawButton(mc, mouseX, mouseY, 0);
	}

	@Override
	public int getDrawWidth(SnitchList list) {
		return buttonWidth;
	}

	@Override
	public List<String> hover(SnitchList item, int xPos, int yPos) {
		return null;
	}

	@Override
	public boolean canSort() {
		return false;
	}

	@Override
	public int compare(SnitchList o1, SnitchList o2) {
		return 0;
	}
}

