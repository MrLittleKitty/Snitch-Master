package com.gmail.nuclearcat1337.snitch_master.gui.snitchtable;

import com.gmail.nuclearcat1337.snitch_master.gui.tables.TableColumn;
import com.gmail.nuclearcat1337.snitch_master.snitches.Snitch;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;

import java.util.List;

/**
 * Created by Mr_Little_Kitty on 1/7/2017.
 */
public class SnitchDescriptionColumn implements TableColumn<Snitch> {
	private static Minecraft mc;

	public SnitchDescriptionColumn() {
		mc = Minecraft.getMinecraft();
	}

	@Override
	public GuiButton[] prepareEntry(Snitch item) {
		return null;
	}

	@Override
	public String getColumnName() {
		return "Description";
	}

	@Override
	public boolean doBoundsCheck() {
		return true;
	}

	@Override
	public void clicked(Snitch item, boolean leftClick, int xPos, int yPos, GuiButton[] buttons, GuiScreen parentScreen, int slotIndex) {

	}

	@Override
	public void released(Snitch item, int xPos, int yPos, GuiButton[] buttons, GuiScreen parentScreen, int slotIndex) {

	}

	@Override
	public void draw(Snitch snitch, int xPos, int yPos, int columnWidth, int slotHeight, GuiButton[] buttons, int slotIndex, int mouseX, int mouseY) {
		String text = snitch.getDescription() == null ? "No Description" : "Hover to view";
		int yFinal = yPos + ((slotHeight - mc.fontRenderer.FONT_HEIGHT) / 2);
		int nameWidth = mc.fontRenderer.getStringWidth(text);
		int namePos = xPos + (columnWidth / 2) - (nameWidth / 2);
		mc.fontRenderer.drawString(text, namePos, yFinal, 16777215);
	}

	@Override
	public int getDrawWidth(Snitch snitch) {
		String text = snitch.getDescription() == null ? "No Description" : "Hover to view";
		return mc.fontRenderer.getStringWidth(text);
	}

	@Override
	public List<String> hover(Snitch snitch, int xPos, int yPos) {
		return snitch.getDescription();
	}

	@Override
	public boolean canSort() {
		return false;
	}

	@Override
	public int compare(Snitch snitch, Snitch other) {
		return 0;
	}
}
