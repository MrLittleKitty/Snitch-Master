package com.gmail.nuclearcat1337.snitch_master.gui.snitchtable;

import com.gmail.nuclearcat1337.snitch_master.Settings;
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
import java.util.List;

public class SnitchesTable extends TableTopGui<Snitch> {
	private static final String SNITCHES_COLUMNS_KEY = "snitches-table-columns";
	private static final int VIEW_LISTS_BUTTON_WIDTH = 60;

	private final SnitchMaster snitchMaster;

	public SnitchesTable(GuiScreen parentScreen, Collection<Snitch> items, String title, SnitchMaster snitchMaster) {
		super(parentScreen, items, title);
		this.snitchMaster = snitchMaster;
	}

	@Override
	protected void initializeButtons(int firstId) {

	}

	@Override
	public void saveColumns(List<TableColumn<Snitch>> allColumns, List<TableColumn<Snitch>> renderColumns) {
		Settings settings = snitchMaster.getSettings();

		String saveString = "";
		for (TableColumn<Snitch> col : allColumns) {
			String entry = col.getColumnName() + "," + renderColumns.contains(col);
			saveString += entry + ";";
		}
		settings.setValue(SNITCHES_COLUMNS_KEY, saveString.substring(0, saveString.length() - 1));
		settings.saveSettings();
	}

	@Override
	protected Collection<Pair<TableColumn<Snitch>, Boolean>> initializeColumns() {
		Settings settings = snitchMaster.getSettings();

		ArrayList<Pair<TableColumn<Snitch>, Boolean>> columns = new ArrayList<>();
		columns.add(packageValues(new SnitchRemoveColumn(snitchMaster), false));

		columns.add(packageValues(new SnitchNameColumn(), true));
		columns.add(packageValues(new SnitchGroupColumn(), true));
		columns.add(packageValues(new SnitchDescriptionColumn(), true));
		columns.add(packageValues(new SnitchCullTimeColumn(), false));

		columns.add(packageValues(new SnitchCoordinateColumn(SnitchCoordinateColumn.CoordinateType.X), false));
		columns.add(packageValues(new SnitchCoordinateColumn(SnitchCoordinateColumn.CoordinateType.Y), false));
		columns.add(packageValues(new SnitchCoordinateColumn(SnitchCoordinateColumn.CoordinateType.Z), false));
		columns.add(packageValues(new SnitchWorldColumn(), false));

		columns.add(packageValues(new SnitchDistanceColumn(), true));
		columns.add(packageValues(new TableButtonColumn<>("Lists", "View", VIEW_LISTS_BUTTON_WIDTH, viewListsClick), true));

		if (!settings.hasValue(SNITCHES_COLUMNS_KEY)) {
			String saveString = "";
			for (Pair<TableColumn<Snitch>, Boolean> pair : columns) {
				String entry = pair.getOne().getColumnName() + "," + pair.getTwo().toString();
				saveString += entry + ";";
			}
			settings.setValue(SNITCHES_COLUMNS_KEY, saveString.substring(0, saveString.length() - 1));
			settings.saveSettings();
		} else {
			String[] entries = settings.getValue(SNITCHES_COLUMNS_KEY).toString().split(";");
			for (int entryIndex = 0; entryIndex < entries.length; entryIndex++) {
				String[] vals = entries[entryIndex].split(",");
				for (int colIndex = 0; colIndex < columns.size(); colIndex++) {
					Pair<TableColumn<Snitch>, Boolean> pair = columns.get(colIndex);
					//Find the column with this name
					if (pair.getOne().getColumnName().equalsIgnoreCase(vals[0])) {
						pair.setValues(pair.getOne(), Boolean.parseBoolean(vals[1]));

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

	private final TableButtonColumn.OnButtonClick<Snitch> viewListsClick = new TableButtonColumn.OnButtonClick<Snitch>() {
		@Override
		public void onClick(Snitch item, GuiButton button, GuiScreen parent) {
			String snitchName = item.getName().isEmpty() ? "Undefined" : item.getName();
			mc.displayGuiScreen(new SnitchListsTable(parent, snitchMaster.getManager().getSnitchListsForSnitch(item), "Snitch Lists for Snitch " + snitchName, false, snitchMaster));
		}
	};

	private static Pair<TableColumn<Snitch>, Boolean> packageValues(TableColumn<Snitch> col, Boolean render) {
		return new Pair<>(col, render);
	}
}
