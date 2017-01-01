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
 * Created by Mr_Little_Kitty on 1/1/2017.
 */
public class SnitchGroupColumn implements TableColumn<Snitch>
{
    private final Minecraft mc;
    private final int columnWidth;

    public SnitchGroupColumn()
    {
        mc = Minecraft.getMinecraft();
        columnWidth = mc.fontRendererObj.getStringWidth(Snitch.MAX_CT_GROUP_NAME_CHARACTERS);
    }

    @Override
    public GuiButton[] prepareEntry(Snitch item)
    {
        return null;
    }

    @Override
    public String getColumnName()
    {
        return "Citadel Group";
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
        return false;
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
        String text = snitch.getGroupName().isEmpty() ? "Undefined" : snitch.getGroupName();
        int yFinal = yPos + ((slotHeight - mc.fontRendererObj.FONT_HEIGHT) /2);
        int nameWidth = mc.fontRendererObj.getStringWidth(text);
        int namePos = xPos + (columnWidth /2) - (nameWidth/2);
        mc.fontRendererObj.drawString(text, namePos ,yFinal, 16777215);
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
}
