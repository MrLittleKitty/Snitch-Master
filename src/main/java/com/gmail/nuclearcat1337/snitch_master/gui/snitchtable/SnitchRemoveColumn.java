package com.gmail.nuclearcat1337.snitch_master.gui.snitchtable;

import com.gmail.nuclearcat1337.snitch_master.SnitchMaster;
import com.gmail.nuclearcat1337.snitch_master.gui.GuiConstants;
import com.gmail.nuclearcat1337.snitch_master.gui.tables.TableColumn;
import com.gmail.nuclearcat1337.snitch_master.locatableobjectlist.ILocation;
import com.gmail.nuclearcat1337.snitch_master.snitches.Snitch;
import com.gmail.nuclearcat1337.snitch_master.snitches.SnitchManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;

import java.util.HashSet;
import java.util.List;

/**
 * Created by Mr_Little_Kitty on 1/3/2017.
 */
public class SnitchRemoveColumn implements TableColumn<Snitch>
{
    private static Minecraft mc;

    private static final String BUTTON_TEXT = "X";
    //private static final int BUTTON_WIDTH = 5;

    private final int buttonWidth;
    private final SnitchManager manager;
    public static final HashSet<ILocation> removedSnitches = new HashSet<>();

    public SnitchRemoveColumn(SnitchMaster snitchMaster)
    {
        mc = Minecraft.getMinecraft();

        buttonWidth = mc.fontRendererObj.getStringWidth(BUTTON_TEXT + "---");
        this.manager = snitchMaster.getManager();
        //removedSnitches = new HashSet<>();
    }

    @Override
    public GuiButton[] prepareEntry(Snitch item)
    {
        GuiButton[] buttons = new GuiButton[1];
        buttons[0] = new GuiButton(0, 0, 0, buttonWidth, GuiConstants.STANDARD_BUTTON_HEIGHT, BUTTON_TEXT);
        return buttons;
    }

    @Override
    public String getColumnName()
    {
        return "Remove";
    }

    @Override
    public boolean doBoundsCheck()
    {
        return true;
    }

    @Override
    public void clicked(Snitch item, boolean leftClick, int xPos, int yPos, GuiButton[] buttons, GuiScreen parentScreen, int slotIndex)
    {
        //Don't allow right clicks on the button
        if (!leftClick || removedSnitches.contains(item.getLocation()))
            return;

        if (buttons[0].mousePressed(mc, xPos, yPos))
        {
            manager.getSnitches().remove(item);

            removedSnitches.add(item.getLocation());

            if (SnitchMaster.jmInterface != null)
                SnitchMaster.jmInterface.refresh(manager.getSnitches());

            manager.saveSnitches();
        }
    }

    @Override
    public void released(Snitch list, int xPos, int yPos, GuiButton[] buttons, GuiScreen parentScreen, int slotIndex)
    {
        if (removedSnitches.contains(list.getLocation()))
            return;

        buttons[0].mouseReleased(xPos, yPos);
    }

    @Override
    public void draw(Snitch list, int xPosition, int yPosition, int columnWidth, int slotHeight, GuiButton[] buttons, int slotIndex, int mouseX, int mouseY)
    {
        if (removedSnitches.contains(list.getLocation()))
            return;

        yPosition = yPosition + ((slotHeight - GuiConstants.STANDARD_BUTTON_HEIGHT) / 2);
        int xPos = xPosition + (columnWidth / 2) - (buttonWidth / 2);

        buttons[0].yPosition = yPosition;
        buttons[0].xPosition = xPos;

        buttons[0].drawButton(mc, mouseX, mouseY);
    }

    @Override
    public int getDrawWidth(Snitch list)
    {
        return buttonWidth;
    }

    @Override
    public List<String> hover(Snitch item, int xPos, int yPos)
    {
        return null;
    }

    @Override
    public boolean canSort()
    {
        return false;
    }

    @Override
    public int compare(Snitch o1, Snitch o2)
    {
        return 0;
    }
}
