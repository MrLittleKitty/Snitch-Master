package com.gmail.nuclearcat1337.snitch_master.gui.snitchtable;

import com.gmail.nuclearcat1337.snitch_master.gui.GuiConstants;
import com.gmail.nuclearcat1337.snitch_master.gui.tables.TableColumn;
import com.gmail.nuclearcat1337.snitch_master.snitches.Snitch;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Mr_Little_Kitty on 1/1/2017.
 */
public class SnitchCoordinateColumn implements TableColumn<Snitch>
{
    public enum CoordinateType
    {
        X,
        Y,
        Z;
    }

    private final Minecraft mc;
    private final int columnWidth;
    private final CoordinateType type;

    public SnitchCoordinateColumn(CoordinateType type)
    {
        mc = Minecraft.getMinecraft();

        this.type = type;

        if(type == CoordinateType.X || type == CoordinateType.Z)
            columnWidth = mc.fontRendererObj.getStringWidth("WWWWW");
        else
            columnWidth = mc.fontRendererObj.getStringWidth("WWW");
    }

    @Override
    public GuiButton[] prepareEntry(Snitch item)
    {
        return null;
    }

    @Override
    public String getColumnName()
    {
        if(type == CoordinateType.X)
            return "X";
        else if(type == CoordinateType.Y)
            return "Y";
        else
            return "Z";
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
        String text;
        if(type == CoordinateType.X)
            text = ""+snitch.getLocation().getX();
        else if(type == CoordinateType.Y)
            text = ""+snitch.getLocation().getY();
        else
            text = ""+snitch.getLocation().getZ();
        int yFinal = yPos + ((slotHeight - mc.fontRendererObj.FONT_HEIGHT) /2);
        int nameWidth = mc.fontRendererObj.getStringWidth(text);
        int namePos = xPos + (columnWidth /2) - (nameWidth/2);
        mc.fontRendererObj.drawString(text, namePos ,yFinal, 16777215);
    }

    @Override
    public List<String> hover(Snitch snitch, int xPos, int yPos)
    {
        List<String> temp = new ArrayList<>(1);
        temp.add(snitch.getWorld());
        return temp;
    }

    @Override
    public int compare(Snitch o1, Snitch o2)
    {
        return 0;
    }
}
