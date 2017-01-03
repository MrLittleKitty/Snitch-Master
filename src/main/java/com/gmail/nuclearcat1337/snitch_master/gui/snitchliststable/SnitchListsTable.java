package com.gmail.nuclearcat1337.snitch_master.gui.snitchliststable;

import com.gmail.nuclearcat1337.snitch_master.SnitchMaster;
import com.gmail.nuclearcat1337.snitch_master.api.SnitchListQualifier;
import com.gmail.nuclearcat1337.snitch_master.gui.EditColorGui;
import com.gmail.nuclearcat1337.snitch_master.gui.EditStringGui;
import com.gmail.nuclearcat1337.snitch_master.gui.GuiConstants;
import com.gmail.nuclearcat1337.snitch_master.gui.NewSnitchListGui;
import com.gmail.nuclearcat1337.snitch_master.gui.snitchtable.*;
import com.gmail.nuclearcat1337.snitch_master.gui.tables.TableButtonColumn;
import com.gmail.nuclearcat1337.snitch_master.gui.tables.TableColumn;
import com.gmail.nuclearcat1337.snitch_master.gui.tables.TableTopGui;
import com.gmail.nuclearcat1337.snitch_master.snitches.Snitch;
import com.gmail.nuclearcat1337.snitch_master.snitches.SnitchList;
import com.gmail.nuclearcat1337.snitch_master.util.Acceptor;
import com.gmail.nuclearcat1337.snitch_master.util.Color;
import com.gmail.nuclearcat1337.snitch_master.util.Pair;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by Mr_Little_Kitty on 1/2/2017.
 */
public class SnitchListsTable extends TableTopGui<SnitchList>
{
    private static final int EDIT_COLOR_BUTTON_WIDTH = 60;
    private static final int EDIT_QUALIFIER_BUTTON_WIDTH = 60;
    private static final int VIEW_SNITCHES_BUTTON_WIDTH = 60;

    private static final int NEW_BUTTON_WIDTH = GuiConstants.SMALL_BUTTON_WIDTH;
    private static final int RENDER_ON_BUTTON_WIDTH = GuiConstants.SMALL_BUTTON_WIDTH;
    private static final int RENDER_OFF_BUTTON_WIDTH = GuiConstants.SMALL_BUTTON_WIDTH;

    private final SnitchMaster snitchMaster;
    private final boolean fullList;

    public SnitchListsTable(GuiScreen parentScreen, Collection<SnitchList> items, String title, boolean fullList, SnitchMaster snitchMaster)
    {
        super(parentScreen, items, title);
        this.fullList = fullList;
        this.snitchMaster = snitchMaster;
    }

    int firstId;
    @Override
    protected void initializeButtons(int firstId)
    {
        this.firstId = firstId;
        int xPos = (this.width/2)+(GuiConstants.STANDARD_SEPARATION_DISTANCE/2);
        int yPos = this.height - GuiConstants.STANDARD_BUTTON_HEIGHT - GuiConstants.STANDARD_SEPARATION_DISTANCE;

        if(fullList)
            this.buttonList.add(new GuiButton(firstId, xPos, yPos, NEW_BUTTON_WIDTH, 18, "New"));

        xPos += GuiConstants.STANDARD_SEPARATION_DISTANCE+ NEW_BUTTON_WIDTH;
        this.buttonList.add(new GuiButton(firstId+1, xPos, yPos, RENDER_ON_BUTTON_WIDTH, 18, "All On"));

        xPos += GuiConstants.STANDARD_SEPARATION_DISTANCE+ RENDER_ON_BUTTON_WIDTH;
        this.buttonList.add(new GuiButton(firstId+2, xPos, yPos, RENDER_OFF_BUTTON_WIDTH, 18, "All Off"));
    }

    public void actionPerformed(GuiButton button)
    {
        if (!button.enabled)
            return;

        if(button.id == firstId) //New snitch list
            Minecraft.getMinecraft().displayGuiScreen(new NewSnitchListGui(this,snitchMaster));
        else if(button.id == firstId+1) //Set all render on
            setAllRender(true);
        else if(button.id == firstId+2) //Set all render off
            setAllRender(false);

        super.actionPerformed(button);
    }

    private void setAllRender(boolean on)
    {
        for(SnitchList list : getItems())
            list.setShouldRenderSnitches(on);
    }

    @Override
    protected Collection<Pair<TableColumn<SnitchList>,Boolean>> initializeColumns()
    {
        ArrayList<Pair<TableColumn<SnitchList>,Boolean>> columns = new ArrayList<>();

        columns.add(packageValues(new SnitchListRemoveColumn(snitchMaster),false));

        columns.add(packageValues(new SnitchListControlsColumn(this),true));
        columns.add(packageValues(new SnitchListNameColumn(snitchMaster.getSnitchLists()),true));

        columns.add(packageValues(new TableButtonColumn<>("Color","Edit",EDIT_COLOR_BUTTON_WIDTH,colorClick),true));
        columns.add(packageValues(new TableButtonColumn<>("Qualifier","Edit",EDIT_QUALIFIER_BUTTON_WIDTH,qualifierClick),true));
        columns.add(packageValues(new TableButtonColumn<>("Snitches","View",VIEW_SNITCHES_BUTTON_WIDTH,viewSnitchesClick),true));

        return columns;
    }

    private final TableButtonColumn.OnButtonClick<SnitchList> qualifierClick = new TableButtonColumn.OnButtonClick<SnitchList>()
    {
        @Override
        public void onClick(SnitchList list,GuiButton button, GuiScreen parentScreen)
        {
            mc.displayGuiScreen(new EditStringGui(parentScreen,list.getQualifier().toString(),"Edit Qualifier",new EditQualifierAcceptor(list),100));
        }
    };

    private final TableButtonColumn.OnButtonClick<SnitchList> colorClick = new TableButtonColumn.OnButtonClick<SnitchList>()
    {
        @Override
        public void onClick(SnitchList list,GuiButton button, GuiScreen parentScreen)
        {
            mc.displayGuiScreen(new EditColorGui(parentScreen,list.getListColor(),"Edit Color",new EditColorAcceptor(list)));
        }
    };

    private final TableButtonColumn.OnButtonClick<SnitchList> viewSnitchesClick = new TableButtonColumn.OnButtonClick<SnitchList>()
    {
        @Override
        public void onClick(SnitchList list,GuiButton button, GuiScreen parentScreen)
        {
            ArrayList<Snitch> snitches = snitchMaster.getSnitchLists().getSnitchesInList(list);
            if(snitches.isEmpty())
                button.displayString = "None";
            else
                mc.displayGuiScreen(new SnitchesTable(parentScreen,snitches,"Snitches for Snitch List: "+list.getListName(),snitchMaster));
        }
    };

    private class EditColorAcceptor implements Acceptor<Color>
    {
        private final SnitchList list;

        private EditColorAcceptor(SnitchList list)
        {
            this.list = list;
        }

        @Override
        public boolean accept(Color item)
        {
            list.setListColor(item);
            snitchMaster.getSnitchLists().snitchListChanged();
            return true;
        }
    }

    private class EditQualifierAcceptor implements Acceptor<String>
    {
        private final SnitchList list;

        private EditQualifierAcceptor(SnitchList list)
        {
            this.list = list;
        }

        @Override
        public boolean accept(String item)
        {
            boolean valid = SnitchListQualifier.isSyntaxValid(item);
            if(valid)
            {
                //Change the qualifier to the new one
                list.getQualifier().updateQualifier(item);

                //We need to clear all links to this snitch list and requalify all snitches
                snitchMaster.getSnitchLists().requalifySnitchList(list);

                //This updates journeymap and other things now that lists/snitches have changed
                snitchMaster.getSnitchLists().snitchListChanged();
                return true;
            }
            return false;
        }
    }

    private static Pair<TableColumn<SnitchList>,Boolean> packageValues(TableColumn<SnitchList> col, Boolean render)
    {
        return new Pair<>(col,render);
    }
}
