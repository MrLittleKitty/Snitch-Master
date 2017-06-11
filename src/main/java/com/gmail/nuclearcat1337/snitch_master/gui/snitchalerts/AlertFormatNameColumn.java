package com.gmail.nuclearcat1337.snitch_master.gui.snitchalerts;

import com.gmail.nuclearcat1337.snitch_master.gui.tables.TableColumn;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;

import java.util.List;

/**
 * Created by Mr_Little_Kitty on 6/5/2017.
 */
public class AlertFormatNameColumn implements TableColumn<AlertFormat>
{
    private static Minecraft mc;

    public AlertFormatNameColumn()
    {
        mc = Minecraft.getMinecraft();
    }

    @Override
    public GuiButton[] prepareEntry(AlertFormat item)
    {
        return null;
    }

    @Override
    public String getColumnName()
    {
        return "Alert Format Name";
    }

    @Override
    public boolean doBoundsCheck()
    {
        return false;
    }

    @Override
    public void clicked(AlertFormat item, boolean leftClick, int xPos, int yPos, GuiButton[] buttons, GuiScreen parentScreen, int slotIndex)
    {

    }

    @Override
    public void released(AlertFormat item, int xPos, int yPos, GuiButton[] buttons, GuiScreen parentScreen, int slotIndex)
    {

    }

    @Override
    public void draw(AlertFormat item, int xPos, int yPos, int columnWidth, int slotHeight, GuiButton[] buttons, int slotIndex, int mouseX, int mouseY)
    {
        String text = item.getName();
        int yFinal = yPos + ((slotHeight - mc.fontRendererObj.FONT_HEIGHT) / 2);
        int nameWidth = mc.fontRendererObj.getStringWidth(text);
        int namePos = xPos + (columnWidth / 2) - (nameWidth / 2);
        mc.fontRendererObj.drawString(text, namePos, yFinal, 16777215);
    }

    @Override
    public int getDrawWidth(AlertFormat item)
    {
        String text = item.getName();
        return mc.fontRendererObj.getStringWidth(text);
    }

    @Override
    public List<String> hover(AlertFormat item, int xPos, int yPos)
    {
        return null;
    }

    @Override
    public boolean canSort()
    {
        return true;
    }

    @Override
    public int compare(AlertFormat first, AlertFormat second)
    {
        return first.getName().compareTo(second.getName());
    }
}
