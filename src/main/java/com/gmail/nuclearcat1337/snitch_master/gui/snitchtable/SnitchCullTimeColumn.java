package com.gmail.nuclearcat1337.snitch_master.gui.snitchtable;

import com.gmail.nuclearcat1337.snitch_master.SnitchMaster;
import com.gmail.nuclearcat1337.snitch_master.gui.GuiConstants;
import com.gmail.nuclearcat1337.snitch_master.gui.tables.TableColumn;
import com.gmail.nuclearcat1337.snitch_master.snitches.Snitch;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.List;

/**
 * Created by Mr_Little_Kitty on 1/1/2017.
 */
public class SnitchCullTimeColumn implements TableColumn<Snitch>
{
    private static final NumberFormat CULL_TIME_FORMAT = new DecimalFormat("#.000");
    private final Minecraft mc;
    private final int columnWidth;

    public SnitchCullTimeColumn()
    {
        mc = Minecraft.getMinecraft();
        columnWidth = mc.fontRendererObj.getStringWidth("WWW.WWWW");
    }

    @Override
    public GuiButton[] prepareEntry(Snitch item)
    {
        return null;
    }

    @Override
    public String getColumnName()
    {
        return "Cull Time";
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
        String text = SnitchMaster.CULL_TIME_ENABLED ? (Double.isNaN(snitch.getCullTime()) ? "Off" : CULL_TIME_FORMAT.format(snitch.getCullTime())) : "Off";
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
