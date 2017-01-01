package com.gmail.nuclearcat1337.snitch_master.gui.snitchtable;

import com.gmail.nuclearcat1337.snitch_master.gui.GuiConstants;
import com.gmail.nuclearcat1337.snitch_master.gui.tables.TableColumn;
import com.gmail.nuclearcat1337.snitch_master.snitches.Snitch;
import com.gmail.nuclearcat1337.snitch_master.snitches.SnitchList;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Mr_Little_Kitty on 12/31/2016.
 */
public class SnitchNameColumn implements TableColumn<Snitch>
{
    private final Minecraft mc;
    private final int columnWidth;
    public SnitchNameColumn()
    {
        mc = Minecraft.getMinecraft();
        columnWidth = mc.fontRendererObj.getStringWidth(Snitch.MAX_NAME_CHARACTERS);
    }

    @Override
    public GuiButton[] prepareEntry(Snitch item)
    {
        return null;
    }

    @Override
    public String getColumnName()
    {
        return "Snitch Name";
    }

    @Override
    public int getColumnWidth()
    {
        return columnWidth;
    }

    @Override
    public int getRightSeparationDistance()
    {
        return GuiConstants.STANDARD_SEPARATION_DISTANCE;
    }

    @Override
    public boolean doBoundsCheck()
    {
        return true;
    }

    @Override
    public void clicked(Snitch item, boolean leftClick, int xPos, int yPos, GuiButton[] buttons)
    {

    }

    @Override
    public void released(Snitch item, int xPos, int yPos, GuiButton[] buttons)
    {

    }

    @Override
    public void draw(Snitch snitch, int xPos, int yPos, int slotHeight, GuiButton[] buttons)
    {
        int yFinal = yPos + ((slotHeight - mc.fontRendererObj.FONT_HEIGHT) /2);
        int nameWidth = mc.fontRendererObj.getStringWidth(snitch.getSnitchName());
        int namePos = xPos + (columnWidth /2) - (nameWidth/2);
        mc.fontRendererObj.drawString(snitch.getSnitchName(), namePos ,yFinal, 16777215);
    }

    @Override
    public List<String> hover(Snitch snitch, int xPos, int yPos)
    {
        List<String> temp = new ArrayList<>(snitch.getAttachedSnitchLists().size()+1);
        temp.add("Snitch Lists:");
        for(SnitchList list : snitch.getAttachedSnitchLists())
            temp.add(list.getListName());
        return temp;
    }

    @Override
    public int compare(Snitch o1, Snitch o2)
    {
        return 0;
    }
}
