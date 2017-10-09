package com.gmail.nuclearcat1337.snitch_master.gui.snitchliststable;

import com.gmail.nuclearcat1337.snitch_master.gui.screens.EditStringGui;
import com.gmail.nuclearcat1337.snitch_master.gui.tables.TableColumn;
import com.gmail.nuclearcat1337.snitch_master.snitches.SnitchList;
import com.gmail.nuclearcat1337.snitch_master.snitches.SnitchManager;
import com.gmail.nuclearcat1337.snitch_master.util.Acceptor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;

import java.util.List;

public class SnitchListNameColumn implements TableColumn<SnitchList> {
	private static Minecraft mc;

	private final SnitchManager manager;

	public SnitchListNameColumn(SnitchManager manager) {
		mc = Minecraft.getMinecraft();
		this.manager = manager;
	}

	@Override
	public GuiButton[] prepareEntry(SnitchList item) {
		return null;
	}

	@Override
	public String getColumnName() {
		return "Name";
	}

	@Override
	public boolean doBoundsCheck() {
		return true;
	}

	@Override
	public void clicked(SnitchList list, boolean leftClick, int xPos, int yPos, GuiButton[] buttons, GuiScreen parentScreen, int slotIndex) {
		if (!leftClick) {
			mc.displayGuiScreen(new EditStringGui(parentScreen, list.getListName(), "Edit List Name", new EditNameAcceptor(list), 20));
		}
	}

	@Override
	public void released(SnitchList list, int xPos, int yPos, GuiButton[] buttons, GuiScreen parentScreen, int slotIndex) {

	}

	@Override
	public void draw(SnitchList list, int xPos, int yPos, int columnWidth, int slotHeight, GuiButton[] buttons, int slotIndex, int mouseX, int mouseY) {
		int stringYPosition = yPos + ((slotHeight - mc.fontRenderer.FONT_HEIGHT) / 2);
		String text = list.getListName();
		int stringWidth = mc.fontRenderer.getStringWidth(text);
		int namePos = xPos + (columnWidth / 2) - (stringWidth / 2);
		mc.fontRenderer.drawString(text, namePos, stringYPosition, 16777215);
	}

	@Override
	public int getDrawWidth(SnitchList list) {
		return mc.fontRenderer.getStringWidth(list.getListName());
	}

	@Override
	public List<String> hover(SnitchList item, int xPos, int yPos) {
		return null;
	}

	@Override
	public boolean canSort() {
		return true;
	}

	@Override
	public int compare(SnitchList list, SnitchList other) {
		return list.getListName().compareTo(other.getListName());
	}

	private class EditNameAcceptor implements Acceptor<String> {
		private final SnitchList list;

		private EditNameAcceptor(SnitchList list) {
			this.list = list;
		}

		@Override
		public boolean accept(String item) {
			//Check to make sure there isn't already a list with the name they provided
			//Note, if they don't change the name and just click OK then this will stop us from unnecessarily updating
			boolean valid = !manager.doesListWithNameExist(item);
			if (valid) {
				list.setListName(item);
				manager.saveSnitchLists();
				return true;
			}
			return false;
		}
	}
}
