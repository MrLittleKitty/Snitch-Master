package com.gmail.nuclearcat1337.snitch_master.gui.snitchtable;

import com.gmail.nuclearcat1337.snitch_master.SnitchMaster;
import com.gmail.nuclearcat1337.snitch_master.gui.GuiConstants;
import com.gmail.nuclearcat1337.snitch_master.gui.tables.TableColumn;
import com.gmail.nuclearcat1337.snitch_master.locatableobjectlist.ILocation;
import com.gmail.nuclearcat1337.snitch_master.snitches.Snitch;
import com.gmail.nuclearcat1337.snitch_master.snitches.SnitchList;
import com.gmail.nuclearcat1337.snitch_master.util.Location;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Mr_Little_Kitty on 1/1/2017.
 */
public class SnitchDistanceColumn implements TableColumn<Snitch>
{
    private static Minecraft mc;

    public SnitchDistanceColumn()
    {
        mc = Minecraft.getMinecraft();
    }

    @Override
    public GuiButton[] prepareEntry(Snitch item)
    {
        return null;
    }

    @Override
    public String getColumnName()
    {
        return "Distance";
    }

    @Override
    public boolean doBoundsCheck()
    {
        return false;
    }

    @Override
    public void clicked(Snitch item, boolean leftClick, int xPos, int yPos, GuiButton[] buttons, GuiScreen parentScreen,int slotIndex)
    {

    }

    @Override
    public void released(Snitch item, int xPos, int yPos, GuiButton[] buttons, GuiScreen parentScreen,int slotIndex)
    {

    }

    @Override
    public void draw(Snitch snitch, int xPos, int yPos, int columnWidth, int slotHeight, GuiButton[] buttons,int slotIndex, int mouseX, int mouseY)
    {
        ILocation loc = snitch.getLocation();

        String text;
        if(!SnitchMaster.instance.getCurrentWorld().equalsIgnoreCase(loc.getWorld()))
            text = "NA";
        else
        {
            int distance = getDistanceFromPlayer(loc.getX(), loc.getY(), loc.getZ());
            text = "" + distance;
        }

        int yFinal = yPos + ((slotHeight - mc.fontRendererObj.FONT_HEIGHT) /2);
        int nameWidth = mc.fontRendererObj.getStringWidth(text);
        int namePos = xPos + (columnWidth /2) - (nameWidth/2);
        mc.fontRendererObj.drawString(text, namePos ,yFinal, 16777215);
    }

    @Override
    public int getDrawWidth(Snitch snitch)
    {
        ILocation loc = snitch.getLocation();

        if(!SnitchMaster.instance.getCurrentWorld().equalsIgnoreCase(loc.getWorld()))
            return mc.fontRendererObj.getStringWidth("NA");

        int distance = getDistanceFromPlayer(loc.getX(),loc.getY(),loc.getZ());
        String text = ""+distance;
        return mc.fontRendererObj.getStringWidth(text);
    }

    @Override
    public List<String> hover(Snitch snitch, int xPos, int yPos)
    {
        return null;
    }

    @Override
    public int compare(Snitch o1, Snitch o2)
    {
        return 0;
    }

    private int getDistanceFromPlayer(int x, int y, int z)
    {
        int x1 = x - (int)mc.thePlayer.posX;
        int y1 = y - (int)mc.thePlayer.posY;
        int z1 = z - (int)mc.thePlayer.posZ;

        return (int)Math.sqrt((x1*x1) + (y1*y1) + (z1*z1));
    }
}
