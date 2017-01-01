package com.gmail.nuclearcat1337.snitch_master.gui.snitchtable;

import com.gmail.nuclearcat1337.snitch_master.gui.GuiConstants;
import com.gmail.nuclearcat1337.snitch_master.gui.tables.TableColumn;
import com.gmail.nuclearcat1337.snitch_master.gui.tables.TableTopGui;
import com.gmail.nuclearcat1337.snitch_master.snitches.Snitch;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by Mr_Little_Kitty on 12/31/2016.
 */
public class SnitchesTable extends TableTopGui<Snitch>
{
    private static final int DONE_BUTTON_WIDTH = GuiConstants.SMALL_BUTTON_WIDTH*3;
    private final GuiScreen parentScreen;

    public SnitchesTable(GuiScreen parentScreen, Collection<Snitch> items, String title)
    {
        super(parentScreen, items, "View Snitches");
        this.parentScreen = parentScreen;
    }

    @Override
    protected void initializeButtons()
    {
        buttonList.clear();

        int xPos = (this.width/2)- DONE_BUTTON_WIDTH - (GuiConstants.STANDARD_SEPARATION_DISTANCE/2);
        int yPos = this.height - GuiConstants.STANDARD_BUTTON_HEIGHT - GuiConstants.STANDARD_SEPARATION_DISTANCE;

        buttonList.add(new GuiButton(1, xPos , yPos, DONE_BUTTON_WIDTH, 18, "Back"));
    }

    @Override
    public void actionPerformed(GuiButton button)
    {
        if (!button.enabled) return;
        switch (button.id)
        {
            case 1: //Done
                this.mc.displayGuiScreen(parentScreen);
                break;
        }
    }

    @Override
    protected Collection<TableColumn<Snitch>> initializeColumns()
    {
        ArrayList<TableColumn<Snitch>> columns = new ArrayList<>();

        columns.add(new SnitchNameColumn());
        return columns;
    }
}
