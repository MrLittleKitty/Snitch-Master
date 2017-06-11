package com.gmail.nuclearcat1337.snitch_master.gui.snitchalerts;

import com.gmail.nuclearcat1337.snitch_master.gui.GuiConstants;
import com.gmail.nuclearcat1337.snitch_master.gui.tables.TableButtonColumn;
import com.gmail.nuclearcat1337.snitch_master.gui.tables.TableColumn;
import com.gmail.nuclearcat1337.snitch_master.gui.tables.TableTopGui;
import com.gmail.nuclearcat1337.snitch_master.snitches.SnitchManager;
import com.gmail.nuclearcat1337.snitch_master.util.Pair;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by Mr_Little_Kitty on 6/5/2017.
 */
public class AlertFormatsTable extends TableTopGui<AlertFormat>
{
    private static final int EDIT_BUTTON_WIDTH = 40;
    private static final int NEW_FORMAT_BUTTON = GuiConstants.MEDIUM_BUTTON_WIDTH;

    private final SnitchManager snitchManager;

    public AlertFormatsTable(SnitchManager snitchManager, GuiScreen parentScreen, Collection<AlertFormat> items)
    {
        super(parentScreen, items, true, "Edit Snitch Alert Formats");
        this.snitchManager = snitchManager;
    }

    int firstId;

    @Override
    protected void initializeButtons(int firstId)
    {
        this.firstId = firstId;
        int xPos = (this.width / 2) + (GuiConstants.STANDARD_SEPARATION_DISTANCE / 2);
        int yPos = this.height - GuiConstants.STANDARD_BUTTON_HEIGHT - GuiConstants.STANDARD_SEPARATION_DISTANCE;

        this.buttonList.add(new GuiButton(firstId,xPos,yPos,NEW_FORMAT_BUTTON,GuiConstants.STANDARD_BUTTON_HEIGHT,"New Format"));
    }

    @Override
    protected Collection<Pair<TableColumn<AlertFormat>, Boolean>> initializeColumns()
    {
        ArrayList<Pair<TableColumn<AlertFormat>,Boolean>> cols = new ArrayList<>();
        cols.add(new Pair<>(new AlertFormatNameColumn(),true));

        TableButtonColumn<AlertFormat> editColumn = new TableButtonColumn<>("Edit","Edit",EDIT_BUTTON_WIDTH,new EditButtonClick());

        cols.add(new Pair<>(editColumn,true));

        return cols;
    }

    private class EditButtonClick implements TableButtonColumn.OnButtonClick<AlertFormat>
    {
        @Override
        public void onClick(AlertFormat item, GuiButton button, GuiScreen parent)
        {

        }
    }
}
