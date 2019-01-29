package com.gmail.nuclearcat1337.snitch_master.gui.snitchtable;

import com.gmail.nuclearcat1337.snitch_master.Settings;
import com.gmail.nuclearcat1337.snitch_master.SnitchMaster;
import com.gmail.nuclearcat1337.snitch_master.gui.snitchliststable.SnitchListsTable;
import com.gmail.nuclearcat1337.snitch_master.gui.tables.TableButtonColumn;
import com.gmail.nuclearcat1337.snitch_master.gui.tables.TableColumn;
import com.gmail.nuclearcat1337.snitch_master.gui.tables.TableTopGui;
import com.gmail.nuclearcat1337.snitch_master.journeymap.JourneyMapInterface;
import com.gmail.nuclearcat1337.snitch_master.snitches.Snitch;
import com.gmail.nuclearcat1337.snitch_master.snitches.SnitchManager;
import com.gmail.nuclearcat1337.snitch_master.util.Pair;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by Mr_Little_Kitty on 12/31/2016.
 */
public class SnitchesTable extends TableTopGui<Snitch> {
    private static final String SNITCHES_COLUMNS_KEY = "snitches-table-columns";
    private static final int VIEW_LISTS_BUTTON_WIDTH = 60;

    private final SnitchManager manager;
    private final Settings settings;
    private final JourneyMapInterface journeyMapInterface;

    public SnitchesTable(GuiScreen parentScreen, Collection<Snitch> items, String title, final SnitchManager manager,
                         final Settings settings, final JourneyMapInterface journeyMapInterface) {
        super(parentScreen, items, title);

        this.manager = manager;
        this.settings = settings;
        this.journeyMapInterface = journeyMapInterface;
    }

    @Override
    protected void initializeButtons(int firstId) {

    }

    @Override
    public void saveColumns(List<TableColumn<Snitch>> allColumns, List<TableColumn<Snitch>> renderColumns) {
        final List<Pair<String, Boolean>> list = new ArrayList<>();
        for (TableColumn<Snitch> col : allColumns) {
            list.add(new Pair<String, Boolean>(col.getColumnName(), renderColumns.contains(col)));
        }
        settings.setSnitchColumns(list);
        settings.saveSettings();
    }

    @Override
    protected Collection<Pair<TableColumn<Snitch>, Boolean>> initializeColumns() {
        ArrayList<Pair<TableColumn<Snitch>, Boolean>> columns = new ArrayList<>();
        columns.add(packageValues(new SnitchRemoveColumn(manager, journeyMapInterface), false));

        columns.add(packageValues(new SnitchNameColumn(), true));
        columns.add(packageValues(new SnitchGroupColumn(), true));
        columns.add(packageValues(new SnitchDescriptionColumn(), true));
        columns.add(packageValues(new SnitchCullTimeColumn(), false));

        columns.add(packageValues(new SnitchCoordinateColumn(SnitchCoordinateColumn.CoordinateType.X), false));
        columns.add(packageValues(new SnitchCoordinateColumn(SnitchCoordinateColumn.CoordinateType.Y), false));
        columns.add(packageValues(new SnitchCoordinateColumn(SnitchCoordinateColumn.CoordinateType.Z), false));
        columns.add(packageValues(new SnitchWorldColumn(), false));

        columns.add(packageValues(new SnitchDistanceColumn(SnitchMaster.instance), true));
        columns.add(packageValues(new TableButtonColumn<>("Lists", "View", VIEW_LISTS_BUTTON_WIDTH, viewListsClick), true));

        if (settings.getSnitchColumns() == null || settings.getSnitchColumns().isEmpty()) {
            final List<Pair<String, Boolean>> list = new ArrayList<>();
            for (Pair<TableColumn<Snitch>, Boolean> pair : columns) {
                list.add(new Pair<String, Boolean>(pair.getOne().getColumnName(), pair.getTwo()));
            }
            settings.setSnitchColumns(list);
            settings.saveSettings();
        } else {
            final List<Pair<String, Boolean>> entries = settings.getSnitchColumns();
            for (int entryIndex = 0; entryIndex < entries.size(); entryIndex++) {
                final Pair<String, Boolean> loadedPair = entries.get(entryIndex);
                for (int colIndex = 0; colIndex < columns.size(); colIndex++) {
                    Pair<TableColumn<Snitch>, Boolean> pair = columns.get(colIndex);
                    //Find the column with this name
                    if (pair.getOne().getColumnName().equalsIgnoreCase(loadedPair.getOne())) {
                        pair.setValues(pair.getOne(), loadedPair.getTwo());

                        //Swap the columns in the list so they are in their proper place
                        Pair<TableColumn<Snitch>, Boolean> temp = columns.get(entryIndex);
                        columns.set(entryIndex, pair);
                        columns.set(colIndex, temp);

                        break;
                    }
                }
            }
        }
        return columns;
    }

    private void saveSnitchColumns() {

    }

    private final TableButtonColumn.OnButtonClick<Snitch> viewListsClick = new TableButtonColumn.OnButtonClick<Snitch>() {
        @Override
        public void onClick(Snitch item, GuiButton button, GuiScreen parent) {
            String snitchName = item.getName().isEmpty() ? "Undefined" : item.getName();
            mc.displayGuiScreen(new SnitchListsTable(parent, manager.getSnitchListsForSnitch(item),
                    "Snitch Lists for Snitch " + snitchName, false, manager, settings));
        }
    };

    private static Pair<TableColumn<Snitch>, Boolean> packageValues(TableColumn<Snitch> col, Boolean render) {
        return new Pair<>(col, render);
    }
}
