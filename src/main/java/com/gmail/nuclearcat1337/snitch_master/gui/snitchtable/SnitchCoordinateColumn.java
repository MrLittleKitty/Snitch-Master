package com.gmail.nuclearcat1337.snitch_master.gui.snitchtable;

import com.gmail.nuclearcat1337.snitch_master.gui.tables.TableColumn;
import com.gmail.nuclearcat1337.snitch_master.snitches.Snitch;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;

import java.util.List;

public class SnitchCoordinateColumn implements TableColumn<Snitch> {
	public enum CoordinateType {
		X, Y, Z;
	}

	private static Minecraft mc;
	private final CoordinateType type;

	public SnitchCoordinateColumn(CoordinateType type) {
		mc = Minecraft.getMinecraft();
		this.type = type;
	}

	@Override
	public GuiButton[] prepareEntry(Snitch item) {
		return null;
	}

	@Override
	public String getColumnName() {
		if (type == CoordinateType.X) {
			return "X";
		} else if (type == CoordinateType.Y) {
			return "Y";
		} else {
			return "Z";
		}
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
		String text = "" + getCoordinate(snitch, type);
		int yFinal = yPos + ((slotHeight - mc.fontRenderer.FONT_HEIGHT) / 2);
		int nameWidth = mc.fontRenderer.getStringWidth(text);
		int namePos = xPos + (columnWidth / 2) - (nameWidth / 2);
		mc.fontRenderer.drawString(text, namePos, yFinal, 16777215);
	}

	@Override
	public int getDrawWidth(Snitch snitch) {
		String text = "" + getCoordinate(snitch, type);
		return mc.fontRenderer.getStringWidth(text);
	}

	@Override
	public List<String> hover(Snitch snitch, int xPos, int yPos) {
		return null;
	}

	@Override
	public boolean canSort() {
		return true;
	}

	@Override
	public int compare(Snitch snitch, Snitch other) {
		return Integer.compare(getCoordinate(snitch, type), getCoordinate(other, type));
	}

	private static int getCoordinate(Snitch snitch, CoordinateType type) {
		if (type == CoordinateType.X) {
			return snitch.getLocation().getX();
		} else if (type == CoordinateType.Z) {
			return snitch.getLocation().getZ();
		} else {
			return snitch.getLocation().getY();
		}
	}
}
