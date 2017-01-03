package com.gmail.nuclearcat1337.snitch_master.gui.snitchtable;

import com.gmail.nuclearcat1337.snitch_master.SnitchMaster;
import com.gmail.nuclearcat1337.snitch_master.gui.snitchliststable.SnitchListsTable;
import com.gmail.nuclearcat1337.snitch_master.gui.tables.TableButtonColumn;
import com.gmail.nuclearcat1337.snitch_master.gui.tables.TableColumn;
import com.gmail.nuclearcat1337.snitch_master.gui.tables.TableTopGui;
import com.gmail.nuclearcat1337.snitch_master.snitches.Snitch;
import com.gmail.nuclearcat1337.snitch_master.util.Pair;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by Mr_Little_Kitty on 12/31/2016.
 */
public class SnitchesTable extends TableTopGui<Snitch>
{
    private static final int VIEW_LISTS_BUTTON_WIDTH = 60;

    private final SnitchMaster snitchMaster;

    public SnitchesTable(GuiScreen parentScreen, Collection<Snitch> items, String title, SnitchMaster snitchMaster)
    {
        super(parentScreen, items, title);
        this.snitchMaster = snitchMaster;
    }

    @Override
    protected void initializeButtons(int firstId)
    {

    }


    @Override
    protected Collection<Pair<TableColumn<Snitch>,Boolean>> initializeColumns()
    {
        ArrayList<Pair<TableColumn<Snitch>,Boolean>> columns = new ArrayList<>();
        columns.add(packageValues(new SnitchNameColumn(),true));
        columns.add(packageValues(new SnitchGroupColumn(),true));
        columns.add(packageValues(new SnitchCullTimeColumn(),false));

        columns.add(packageValues(new SnitchCoordinateColumn(SnitchCoordinateColumn.CoordinateType.X),true));
        columns.add(packageValues(new SnitchCoordinateColumn(SnitchCoordinateColumn.CoordinateType.Y),true));
        columns.add(packageValues(new SnitchCoordinateColumn(SnitchCoordinateColumn.CoordinateType.Z),true));
        columns.add(packageValues(new SnitchWorldColumn(),true));

        columns.add(packageValues(new SnitchDistanceColumn(),true));
        columns.add(packageValues(new TableButtonColumn<>("Lists","View",VIEW_LISTS_BUTTON_WIDTH,viewListsClick),true));
        return columns;
    }

    private final TableButtonColumn.OnButtonClick<Snitch> viewListsClick = new TableButtonColumn.OnButtonClick<Snitch>()
    {
        @Override
        public void onClick(Snitch item, GuiButton button, GuiScreen parent)
        {
            String snitchName = item.getSnitchName().isEmpty() ? "Undefined" : item.getSnitchName();
            mc.displayGuiScreen(new SnitchListsTable(parent,item.getAttachedSnitchLists(),"Snitch Lists for Snitch "+snitchName,false,snitchMaster));
        }
    };

    private static Pair<TableColumn<Snitch>,Boolean> packageValues(TableColumn<Snitch> col, Boolean render)
    {
        return new Pair<>(col,render);
    }
}
