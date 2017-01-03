package com.gmail.nuclearcat1337.snitch_master.gui.snitchtable;

import com.gmail.nuclearcat1337.snitch_master.gui.tables.TableColumn;
import com.gmail.nuclearcat1337.snitch_master.gui.tables.TableTopGui;
import com.gmail.nuclearcat1337.snitch_master.snitches.Snitch;
import com.gmail.nuclearcat1337.snitch_master.util.Pair;
import net.minecraft.client.gui.GuiScreen;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by Mr_Little_Kitty on 12/31/2016.
 */
public class SnitchesTable extends TableTopGui<Snitch>
{
    public SnitchesTable(GuiScreen parentScreen, Collection<Snitch> items, String title)
    {
        super(parentScreen, items, "View Snitches");
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

        columns.add(packageValues(new SnitchDistanceColumn(),true));
        return columns;
    }

    private static Pair<TableColumn<Snitch>,Boolean> packageValues(TableColumn<Snitch> col, Boolean render)
    {
        return new Pair<>(col,render);
    }
}
