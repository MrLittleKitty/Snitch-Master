package com.gmail.nuclearcat1337.snitch_master.gui.snitchtable;

import com.gmail.nuclearcat1337.snitch_master.SnitchMaster;
import com.gmail.nuclearcat1337.snitch_master.gui.tables.TableColumn;
import com.gmail.nuclearcat1337.snitch_master.locatableobjectlist.ILocation;
import com.gmail.nuclearcat1337.snitch_master.snitches.Snitch;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;

import java.util.List;

public class SnitchDistanceColumn implements TableColumn<Snitch> {
	private static Minecraft mc;

	public SnitchDistanceColumn() {
		mc = Minecraft.getMinecraft();
	}

	@Override
	public GuiButton[] prepareEntry(Snitch item) {
		return null;
	}

	@Override
	public String getColumnName() {
		return "Distance";
	}

	@Override
	public boolean doBoundsCheck() {
		return false;
	}

	@Override
	public void clicked(Snitch item, boolean leftClick, int xPos, int yPos, GuiButton[] buttons, GuiScreen parentScreen, int slotIndex) {

	}

	@Override
	public void released(Snitch item, int xPos, int yPos, GuiButton[] buttons, GuiScreen parentScreen, int slotIndex) {

	}

	@Override
	public void draw(Snitch snitch, int xPos, int yPos, int columnWidth, int slotHeight, GuiButton[] buttons, int slotIndex, int mouseX, int mouseY) {
		ILocation loc = snitch.getLocation();

		String text;
		if (!SnitchMaster.instance.getCurrentWorld().equalsIgnoreCase(loc.getWorld())) {
			text = "NA";
		} else {
			int distance = getDistanceFromPlayer(loc.getX(), loc.getY(), loc.getZ());
			text = "" + distance;
		}

		int yFinal = yPos + ((slotHeight - mc.fontRenderer.FONT_HEIGHT) / 2);
		int nameWidth = mc.fontRenderer.getStringWidth(text);
		int namePos = xPos + (columnWidth / 2) - (nameWidth / 2);
		mc.fontRenderer.drawString(text, namePos, yFinal, 16777215);
	}

	@Override
	public int getDrawWidth(Snitch snitch) {
		ILocation loc = snitch.getLocation();

		if (!SnitchMaster.instance.getCurrentWorld().equalsIgnoreCase(loc.getWorld())) {
			return mc.fontRenderer.getStringWidth("NA");
		}

		int distance = getDistanceFromPlayer(loc.getX(), loc.getY(), loc.getZ());
		String text = "" + distance;
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
		ILocation snitchLoc = snitch.getLocation();
		ILocation otherLoc = other.getLocation();
		String world = SnitchMaster.instance.getCurrentWorld();
		boolean b1 = snitchLoc.getWorld().equalsIgnoreCase(world);
		boolean b2 = otherLoc.getWorld().equalsIgnoreCase(world);

		//The first snitch is in the correct world and the other snitch isn't
		if (b1 && !b2) {
			return 1; //The first snitch is greater than the second
		} else if (!b1 && b2) {
			return -1; //Second snitch is greater if its in the correct world and the first one isnt
		} else if (!b1 && !b2) { //If they both arent in the correct world then they are equal
			return 0;
		} else {
			//Both snitches are in the correct world so we compare their actual distances
			return Integer.compare(getDistanceFromPlayer(snitchLoc.getX(), snitchLoc.getY(), snitchLoc.getZ()), getDistanceFromPlayer(otherLoc.getX(), otherLoc.getY(), otherLoc.getZ()));
		}
	}

	private int getDistanceFromPlayer(int x, int y, int z) {
		int x1 = x - (int) mc.player.posX;
		int y1 = y - (int) mc.player.posY;
		int z1 = z - (int) mc.player.posZ;
		return (int) Math.sqrt((x1 * x1) + (y1 * y1) + (z1 * z1));
	}
}
